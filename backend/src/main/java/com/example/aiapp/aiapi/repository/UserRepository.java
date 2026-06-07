package com.example.aiapp.aiapi.repository;

import com.example.aiapp.aiapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /*
     * Used during registration.
     * We check if email already exists.
     */


    // Case insensitive search
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    // Case insensitive existence check
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmail(@Param("email") String email);
}
