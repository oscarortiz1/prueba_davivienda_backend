package com.davivienda.survey.infrastructure.adapter;

import com.davivienda.survey.domain.model.User;
import com.davivienda.survey.domain.port.UserRepository;
import com.google.firebase.database.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Repository
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
@Slf4j
public class FirebaseUserRepository implements UserRepository {
    
    private static final String COLLECTION_NAME = "users";
    
    private DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }
    
    @Override
    public User save(User user) {
        try {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("password", user.getPassword());
            userData.put("createdAt", user.getCreatedAt().toString());
            userData.put("updatedAt", user.getUpdatedAt().toString());
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .child(user.getId())
                    .setValue(userData, (error, ref) -> {
                        if (error != null) {
                            future.completeExceptionally(error.toException());
                        } else {
                            future.complete(null);
                        }
                    });
            
            future.get();
            return user;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving user", e);
            throw new RuntimeException("Error saving user", e);
        }
    }
    
    @Override
    public Optional<User> findById(String id) {
        try {
            CompletableFuture<Optional<User>> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                future.complete(Optional.empty());
                                return;
                            }
                            
                            @SuppressWarnings("unchecked")
                            Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                            future.complete(Optional.of(mapToUser(data)));
                        }
                        
                        @Override
                        public void onCancelled(DatabaseError error) {
                            future.completeExceptionally(error.toException());
                        }
                    });
            
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding user by id", e);
            throw new RuntimeException("Error finding user", e);
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        try {
            CompletableFuture<Optional<User>> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .orderByChild("email")
                    .equalTo(email)
                    .limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                future.complete(Optional.empty());
                                return;
                            }
                            
                            DataSnapshot firstChild = snapshot.getChildren().iterator().next();
                            @SuppressWarnings("unchecked")
                            Map<String, Object> data = (Map<String, Object>) firstChild.getValue();
                            future.complete(Optional.of(mapToUser(data)));
                        }
                        
                        @Override
                        public void onCancelled(DatabaseError error) {
                            future.completeExceptionally(error.toException());
                        }
                    });
            
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding user by email", e);
            throw new RuntimeException("Error finding user", e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
    
    @Override
    public void deleteById(String id) {
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .child(id)
                    .removeValue((error, ref) -> {
                        if (error != null) {
                            future.completeExceptionally(error.toException());
                        } else {
                            future.complete(null);
                        }
                    });
            
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error deleting user", e);
            throw new RuntimeException("Error deleting user", e);
        }
    }
    
    private User mapToUser(Map<String, Object> data) {
        return User.builder()
                .id((String) data.get("id"))
                .name((String) data.get("name"))
                .email((String) data.get("email"))
                .password((String) data.get("password"))
                .createdAt(java.time.LocalDateTime.parse((String) data.get("createdAt")))
                .updatedAt(java.time.LocalDateTime.parse((String) data.get("updatedAt")))
                .build();
    }
}
