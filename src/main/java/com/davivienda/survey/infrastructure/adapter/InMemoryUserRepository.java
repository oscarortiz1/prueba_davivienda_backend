package com.davivienda.survey.infrastructure.adapter;

import com.davivienda.survey.domain.model.User;
import com.davivienda.survey.domain.port.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    
    @Override
    public User save(User user) {
        log.info("Saving user in memory: {}", user.getEmail());
        usersById.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }
    
    @Override
    public Optional<User> findById(String id) {
        log.info("Finding user by id in memory: {}", id);
        return Optional.ofNullable(usersById.get(id));
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Finding user by email in memory: {}", email);
        return Optional.ofNullable(usersByEmail.get(email));
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(email);
    }
    
    @Override
    public void deleteById(String id) {
        log.info("Deleting user from memory: {}", id);
        User user = usersById.remove(id);
        if (user != null) {
            usersByEmail.remove(user.getEmail());
        }
    }
}
