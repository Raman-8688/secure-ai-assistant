package com.example.aiapp.aiapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * User entity represents registered users in our AI Assistant app.
 *
 * This class will create a database table named "users".
 * Each row means one registered user.
 *
 * We store:
 * - name
 * - email
 * - encrypted password
 * - email verification status
 * - OTP for email verification
 * - role for future authorization
 */
@Entity
@Table(name = "users")
public class User {

    /*
     * Primary key.
     * AUTO means database will generate id automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * User full name.
     */
    @Column(nullable = false)
    private String name;

    /*
     * Email should be unique because login will happen using email.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /*
     * Password will not be stored as plain text.
     * We will store BCrypt encrypted password.
     */
    @Column(nullable = false)
    private String password;

    /*
     * Normal user role.
     * Later we can extend this to ADMIN, PREMIUM_USER, etc.
     */
    @Column(nullable = false)
    private String role = "USER";

    /*
     * False means user registered but email not verified yet.
     * True means user verified OTP successfully.
     */
    @Column(nullable = false)
    private boolean emailVerified = false;

    /*
     * OTP code sent to user email.
     */
    private String verificationOtp;

    /*
     * OTP expiry time.
     * After this time, OTP should not be accepted.
     */
    private LocalDateTime otpExpiryTime;

    /*
     * Account creation time.
     */
    private LocalDateTime createdAt;

    /*
     * Automatically set createdAt before saving user first time.
     */
    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
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
}
