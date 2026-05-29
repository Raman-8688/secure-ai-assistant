package com.example.aiapp.aiapi.service;

import com.example.aiapp.aiapi.dto.*;
import com.example.aiapp.aiapi.entity.User;
import com.example.aiapp.aiapi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/*
 * AuthService contains complete authentication business logic.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    /*
     * BCrypt encrypts password securely.
     */
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    /*
     * Register new user.
     */
    public String register(RegisterRequest request) {

        /*
         * Check email already exists.
         */
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already registered";
        }

        /*
         * Create new user object.
         */
        User user = new User();

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        /*
         * Encrypt password before saving.
         */
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        /*
         * Generate 6-digit OTP.
         */
        String otp = String.valueOf(
                100000 + new Random().nextInt(900000)
        );

        user.setVerificationOtp(otp);

        /*
         * OTP valid for 10 minutes.
         */
        user.setOtpExpiryTime(
                LocalDateTime.now().plusMinutes(10)
        );

        /*
         * Save user into database.
         */
        userRepository.save(user);

        /*
         * Send OTP email.
         */
        emailService.sendOtpEmail(
                user.getEmail(),
                otp
        );

        return "Registration successful. Please verify your email.";
    }

    /*
     * Verify email using OTP.
     */
    public String verifyEmail(VerifyEmailRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return "User not found";
        }

        /*
         * Check OTP.
         */
        if (!request.getOtp().equals(user.getVerificationOtp())) {
            return "Invalid OTP";
        }

        /*
         * Check OTP expiry.
         */
        if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            return "OTP expired";
        }

        /*
         * Mark email verified.
         */
        user.setEmailVerified(true);

        /*
         * Clear OTP after successful verification.
         */
        user.setVerificationOtp(null);

        userRepository.save(user);

        return "Email verified successfully";
    }

    /*
     * Login user.
     */
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return new AuthResponse(
                    null,
                    "Invalid email or password"
            );
        }

        /*
         * Verify password.
         */
        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            return new AuthResponse(
                    null,
                    "Invalid email or password"
            );
        }

        /*
         * Check email verification.
         */
        if (!user.isEmailVerified()) {

            return new AuthResponse(
                    null,
                    "Please verify your email first"
            );
        }

        /*
         * Generate JWT token.
         */
        String token =
                jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                "Login successful"
        );
    }

    /*
     * Resend OTP.
     */
    public String resendOtp(ResendOtpRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return "User not found";
        }

        String otp = String.valueOf(
                100000 + new Random().nextInt(900000)
        );

        user.setVerificationOtp(otp);

        user.setOtpExpiryTime(
                LocalDateTime.now().plusMinutes(10)
        );

        userRepository.save(user);

        emailService.sendOtpEmail(
                user.getEmail(),
                otp
        );

        return "OTP resent successfully";
    }
}