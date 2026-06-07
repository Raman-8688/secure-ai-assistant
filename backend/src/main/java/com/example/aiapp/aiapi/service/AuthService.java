package com.example.aiapp.aiapi.service;

import com.example.aiapp.aiapi.dto.*;
import com.example.aiapp.aiapi.entity.User;
import com.example.aiapp.aiapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String register(RegisterRequest request) {
        log.info("=== REGISTRATION START ===");
        log.info("Email: {}", request.getEmail());

        // Validate input
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return "Email is required";
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return "Password must be at least 6 characters";
        }

        // Check existing user - CASE INSENSITIVE
        String emailLower = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(emailLower)) {
            log.warn("Email already exists: {}", emailLower);
            return "Email already registered";
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(emailLower); // Store email in lowercase

        // ENCODE THE PASSWORD
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        log.info("Password encoded for: {}", emailLower);

        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        user.setEmailVerified(false);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        // Save to database
        userRepository.save(user);
        log.info("User saved with ID: {}", user.getId());

        // SEND OTP EMAIL ONLY - NOT IN RESPONSE
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
            log.info("OTP email sent to: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
        }

        log.info("=== REGISTRATION COMPLETE ===");
        // IMPORTANT: Don't return OTP in response
        return "Registration successful. Please check your email for OTP verification.";
    }

    public String verifyEmail(VerifyEmailRequest request) {
        log.info("=== VERIFY EMAIL START ===");
        log.info("Email: {}", request.getEmail());

        String emailLower = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(emailLower).orElse(null);

        if (user == null) {
            log.warn("User not found: {}", emailLower);
            return "User not found";
        }

        if (user.isEmailVerified()) {
            log.info("Email already verified: {}", emailLower);
            return "Email already verified";
        }

        // Validate OTP properly
        if (user.getVerificationOtp() == null) {
            log.error("No OTP found for user: {}", emailLower);
            return "No OTP found. Please request a new OTP.";
        }

        if (!user.getVerificationOtp().equals(request.getOtp())) {
            log.warn("OTP mismatch for: {}", emailLower);
            return "Invalid OTP. Please check and try again.";
        }

        // Check expiry
        if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            log.warn("OTP expired for: {}", emailLower);
            return "OTP has expired. Please request a new OTP.";
        }

        // Mark as verified
        user.setEmailVerified(true);
        user.setVerificationOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);

        log.info("Email verified successfully: {}", emailLower);
        log.info("=== VERIFY EMAIL COMPLETE ===");
        return "Email verified successfully. You can now login.";
    }

    public AuthResponse login(LoginRequest request) {
        log.info("=== LOGIN START ===");
        log.info("Email: {}", request.getEmail());

        String emailLower = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(emailLower).orElse(null);

        if (user == null) {
            log.warn("User not found: {}", emailLower);
            return new AuthResponse(null, "Invalid email or password");
        }

        // Check password
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            log.warn("Invalid password for: {}", emailLower);
            return new AuthResponse(null, "Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            log.warn("Email not verified: {}", emailLower);
            return new AuthResponse(null, "Please verify your email first. Check your inbox for OTP.");
        }

        // Generate token
        String token = jwtService.generateToken(user.getEmail());
        log.info("Login successful for: {}", emailLower);
        log.info("=== LOGIN COMPLETE ===");

        return new AuthResponse(token, "Login successful");
    }

    public String resendOtp(ResendOtpRequest request) {
        log.info("=== RESEND OTP START ===");
        log.info("Email: {}", request.getEmail());

        String emailLower = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(emailLower).orElse(null);

        if (user == null) {
            log.warn("User not found: {}", emailLower);
            return "User not found";
        }

        if (user.isEmailVerified()) {
            log.info("Email already verified: {}", emailLower);
            return "Email already verified. Please login.";
        }

        // Generate new OTP
        String newOtp = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationOtp(newOtp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // Send email only
        emailService.sendOtpEmail(user.getEmail(), newOtp);
        log.info("New OTP sent to: {}", request.getEmail());
        log.info("=== RESEND OTP COMPLETE ===");

        return "OTP resent successfully. Please check your email.";
    }
}