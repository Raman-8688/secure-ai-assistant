// entity/User.java
package com.example.aiapp.aiapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String role = "USER";

    @Column(nullable = false)
    private boolean emailVerified = false;

    private String verificationOtp;

    private LocalDateTime otpExpiryTime;

    private LocalDateTime createdAt;

    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // ========== FORGOT PASSWORD FIELDS ==========

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "last_password_reset")
    private LocalDateTime lastPasswordReset;

    @Column(name = "password_reset_attempts")
    private Integer passwordResetAttempts = 0;

    @Column(name = "reset_token_generated_at")
    private LocalDateTime resetTokenGeneratedAt;

    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
        if (this.passwordResetAttempts == null) {
            this.passwordResetAttempts = 0;
        }
    }

    // ========== GETTERS AND SETTERS ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getVerificationOtp() {
        return verificationOtp;
    }

    public void setVerificationOtp(String verificationOtp) {
        this.verificationOtp = verificationOtp;
    }

    public LocalDateTime getOtpExpiryTime() {
        return otpExpiryTime;
    }

    public void setOtpExpiryTime(LocalDateTime otpExpiryTime) {
        this.otpExpiryTime = otpExpiryTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public LocalDateTime getLastPasswordReset() {
        return lastPasswordReset;
    }

    public void setLastPasswordReset(LocalDateTime lastPasswordReset) {
        this.lastPasswordReset = lastPasswordReset;
    }

    public Integer getPasswordResetAttempts() {
        return passwordResetAttempts;
    }

    public void setPasswordResetAttempts(Integer passwordResetAttempts) {
        this.passwordResetAttempts = passwordResetAttempts;
    }

    public LocalDateTime getResetTokenGeneratedAt() {
        return resetTokenGeneratedAt;
    }

    public void setResetTokenGeneratedAt(LocalDateTime resetTokenGeneratedAt) {
        this.resetTokenGeneratedAt = resetTokenGeneratedAt;
    }
}