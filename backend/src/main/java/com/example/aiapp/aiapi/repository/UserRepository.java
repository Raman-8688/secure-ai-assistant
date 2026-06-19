// repository/UserRepository.java
package com.example.aiapp.aiapi.repository;

import com.example.aiapp.aiapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.passwordResetAttempts = u.passwordResetAttempts + 1 WHERE u.email = :email")
    void incrementResetAttempts(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.passwordResetAttempts = 0 WHERE u.id = :userId")
    void resetResetAttempts(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.resetToken = :token, u.resetTokenExpiry = :expiry, u.resetTokenGeneratedAt = :generatedAt WHERE u.email = :email")
    void updateResetToken(@Param("email") String email,
                          @Param("token") String token,
                          @Param("expiry") LocalDateTime expiry,
                          @Param("generatedAt") LocalDateTime generatedAt);
}