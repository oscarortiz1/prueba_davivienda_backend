package com.davivienda.survey.domain.port;

import com.davivienda.survey.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteById(String id);
}
