package com.davivienda.survey.infrastructure.adapter;

import com.davivienda.survey.domain.model.User;
import com.davivienda.survey.domain.port.UserRepository;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
@Slf4j
public class FirebaseUserRepository implements UserRepository {
    
    private static final String COLLECTION_NAME = "users";
    
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
    
    @Override
    public User save(User user) {
        try {
            Map<String, Object> userData = Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "password", user.getPassword(),
                    "createdAt", user.getCreatedAt().toString(),
                    "updatedAt", user.getUpdatedAt().toString()
            );
            
            getFirestore()
                    .collection(COLLECTION_NAME)
                    .document(user.getId())
                    .set(userData)
                    .get();
            
            return user;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving user", e);
            throw new RuntimeException("Error saving user", e);
        }
    }
    
    @Override
    public Optional<User> findById(String id) {
        try {
            var document = getFirestore()
                    .collection(COLLECTION_NAME)
                    .document(id)
                    .get()
                    .get();
            
            if (!document.exists()) {
                return Optional.empty();
            }
            
            return Optional.of(documentToUser(document.getData()));
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding user by id", e);
            throw new RuntimeException("Error finding user", e);
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        try {
            var querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get()
                    .get();
            
            if (querySnapshot.isEmpty()) {
                return Optional.empty();
            }
            
            return Optional.of(documentToUser(querySnapshot.getDocuments().get(0).getData()));
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
            getFirestore()
                    .collection(COLLECTION_NAME)
                    .document(id)
                    .delete()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error deleting user", e);
            throw new RuntimeException("Error deleting user", e);
        }
    }
    
    private User documentToUser(Map<String, Object> data) {
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
