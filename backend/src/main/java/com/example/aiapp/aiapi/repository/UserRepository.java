
package com.example.aiapp.aiapi.repository;

import com.example.aiapp.aiapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ========== FIND METHODS ==========

    // Case insensitive search
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    // Find by reset token
    Optional<User> findByResetToken(String resetToken);

    // Case insensitive existence check
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmail(@Param("email") String email);

    // ========== RESET PASSWORD METHODS ==========

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
    void updateResetToken(
            @Param("email") String email,
            @Param("token") String token,
            @Param("expiry") LocalDateTime expiry,
            @Param("generatedAt") LocalDateTime generatedAt
    );

    // ========== LAST LOGIN UPDATE ==========

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.email = :email")
    void updateLastLogin(@Param("email") String email, @Param("lastLogin") LocalDateTime lastLogin);

    // ========== VERIFICATION METHODS ==========

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.emailVerified = true, u.verificationOtp = null, u.otpExpiryTime = null WHERE u.email = :email")
    void markEmailVerified(@Param("email") String email);

    // ========== CLEANUP METHODS ==========

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.resetToken = null, u.resetTokenExpiry = null WHERE u.resetTokenExpiry < :now")
    void clearExpiredResetTokens(@Param("now") LocalDateTime now);
}