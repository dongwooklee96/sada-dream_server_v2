package com.sadadream.infra;

import com.sadadream.domain.User;
import com.sadadream.domain.UserRepository;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaUserRepository
        extends UserRepository, CrudRepository<User, Long> {
    User save(User user);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByIdAndDeletedIsFalse(Long id);

    Optional<User> findByEmail(String email);
}
