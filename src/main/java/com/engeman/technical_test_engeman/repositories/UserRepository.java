package com.engeman.technical_test_engeman.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.engeman.technical_test_engeman.domain.User;



public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);
}
