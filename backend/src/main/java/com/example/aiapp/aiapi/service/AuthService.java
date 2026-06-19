// service/AuthService.java
package com.example.aiapp.aiapi.service;

import com.example.aiapp.aiapi.dto.*;
import com.example.aiapp.aiapi.entity.User;
import com.example.aiapp.aiapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${app.reset.token.expiry.minutes:15}")
    private int resetTokenExpiryMinutes;

    @Value("${app.max.reset.attempts:5}")
    private int maxResetAttempts;

    // ==================== REGISTRATION ====================

    public String register(RegisterRequest request) {
        log.info("=== REGISTRATION START ===");
        log.info("Email: {}", request.getEmail());

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return "Email is required";
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return "Password must be at least 6 characters";
        }

        String emailLower = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(emailLower)) {
            log.warn("Email already exists: {}", emailLower);
            return "Email already registered";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(emailLower);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        user.setEmailVerified(false);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("User saved with ID: {}", user.getId());

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
            log.info("OTP email sent to: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
        }

        log.info("=== REGISTRATION COMPLETE ===");
        return "Registration successful. Please check your email for OTP verification.";
    }

    // ==================== EMAIL VERIFICATION ====================

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

        if (user.getVerificationOtp() == null) {
            log.error("No OTP found for user: {}", emailLower);
            return "No OTP found. Please request a new OTP.";
        }

        if (!user.getVerificationOtp().equals(request.getOtp())) {
            log.warn("OTP mismatch for: {}", emailLower);
            return "Invalid OTP. Please check and try again.";
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            log.warn("OTP expired for: {}", emailLower);
            return "OTP has expired. Please request a new OTP.";
        }

        user.setEmailVerified(true);
        user.setVerificationOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);

        log.info("Email verified successfully: {}", emailLower);
        log.info("=== VERIFY EMAIL COMPLETE ===");
        return "Email verified successfully. You can now login.";
    }

    // ==================== LOGIN ====================

    public AuthResponse login(LoginRequest request) {
        log.info("=== LOGIN START ===");
        log.info("Email: {}", request.getEmail());

        String emailLower = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(emailLower).orElse(null);

        if (user == null) {
            log.warn("User not found: {}", emailLower);
            return new AuthResponse(null, "Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for: {}", emailLower);
            return new AuthResponse(null, "Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            log.warn("Email not verified: {}", emailLower);
            return new AuthResponse(null, "Please verify your email first. Check your inbox for OTP.");
        }

        String token = jwtService.generateToken(user.getEmail());
        log.info("Login successful for: {}", emailLower);
        log.info("=== LOGIN COMPLETE ===");

        return new AuthResponse(token, "Login successful");
    }

    // ==================== RESEND OTP ====================

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

        String newOtp = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationOtp(newOtp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), newOtp);
        log.info("New OTP sent to: {}", request.getEmail());
        log.info("=== RESEND OTP COMPLETE ===");

        return "OTP resent successfully. Please check your email.";
    }

    // ==================== FORGOT PASSWORD ====================

    @Transactional
    public ResetPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("=== FORGOT PASSWORD START ===");
        log.info("Email: {}", request.getEmail());

        String emailLower = request.getEmail().toLowerCase().trim();
        Optional<User> userOptional = userRepository.findByEmail(emailLower);

        if (!userOptional.isPresent()) {
            log.warn("Password reset requested for non-existent email: {}", emailLower);
            return new ResetPasswordResponse(
                    "This email is not registered. Please create an account first.",
                    false,
                    "email_not_found"
            );
        }

        User user = userOptional.get();

        if (user.getPasswordResetAttempts() != null &&
                user.getPasswordResetAttempts() >= maxResetAttempts) {
            log.warn("Too many reset attempts for: {}", emailLower);
            return new ResetPasswordResponse(
                    "Too many reset attempts. Please try again after 24 hours.",
                    false,
                    "too_many_attempts"
            );
        }

        String resetToken = generateSecureToken();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(resetTokenExpiryMinutes);

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(expiryTime);
        user.setResetTokenGeneratedAt(LocalDateTime.now());
        userRepository.save(user);

        userRepository.incrementResetAttempts(emailLower);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

        log.info("Password reset token generated for: {}", emailLower);
        log.info("=== FORGOT PASSWORD COMPLETE ===");

        return new ResetPasswordResponse(
                "Password reset link sent to your email. Please check your inbox.",
                true,
                "success"
        );
    }

    // ==================== RESET PASSWORD ====================

    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        log.info("=== RESET PASSWORD START ===");

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Passwords do not match");
            return new ResetPasswordResponse("Passwords do not match.", false, "error");
        }

        if (!isValidPassword(request.getNewPassword())) {
            log.warn("Password does not meet security requirements");
            return new ResetPasswordResponse(
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.",
                    false,
                    "error"
            );
        }

        User user = userRepository.findByResetToken(request.getToken()).orElse(null);

        if (user == null) {
            log.warn("Invalid reset token: {}", request.getToken());
            return new ResetPasswordResponse("Invalid or expired reset token.", false, "error");
        }

        if (LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            log.warn("Expired reset token for: {}", user.getEmail());
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return new ResetPasswordResponse(
                    "Reset token has expired. Please request a new one.",
                    false,
                    "error"
            );
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            log.warn("User trying to reuse old password: {}", user.getEmail());
            return new ResetPasswordResponse(
                    "New password must be different from your current password.",
                    false,
                    "error"
            );
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.setLastPasswordReset(LocalDateTime.now());
        user.setPasswordResetAttempts(0);
        userRepository.save(user);

        log.info("Password reset successful for: {}", user.getEmail());
        log.info("=== RESET PASSWORD COMPLETE ===");

        return new ResetPasswordResponse(
                "Password reset successful. You can now login with your new password.",
                true,
                "success"
        );
    }

    // ==================== VALIDATE TOKEN ====================

    @Transactional
    public ResetPasswordResponse validateResetToken(String token) {
        log.info("=== VALIDATE RESET TOKEN START ===");

        User user = userRepository.findByResetToken(token).orElse(null);

        if (user == null) {
            return new ResetPasswordResponse("Invalid reset token.", false, "error");
        }

        if (LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return new ResetPasswordResponse(
                    "Reset token has expired. Please request a new one.",
                    false,
                    "error"
            );
        }

        log.info("Reset token is valid for: {}", user.getEmail());
        log.info("=== VALIDATE RESET TOKEN COMPLETE ===");

        return new ResetPasswordResponse("Token is valid.", true, "success");
    }

    // ==================== PRIVATE METHODS ====================

    private String generateSecureToken() {
        return UUID.randomUUID().toString() +
                "-" +
                System.currentTimeMillis() +
                "-" +
                UUID.randomUUID().toString().substring(0, 8);
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }
}