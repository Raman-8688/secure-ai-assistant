package com.example.aiapp.aiapi.repository;

import com.example.aiapp.aiapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * UserRepository talks to the users table.
 *
 * JpaRepository gives ready-made methods:
 * - save()
 * - findById()
 * - findAll()
 * - delete()
 *
 * We add custom methods for email-based login and registration checks.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /*
     * Used during login.
     * We search user by email.
     */
    Optional<User> findByEmail(String email);

    /*
     * Used during registration.
     * We check if email already exists.
     */
    boolean existsByEmail(String email);
}
