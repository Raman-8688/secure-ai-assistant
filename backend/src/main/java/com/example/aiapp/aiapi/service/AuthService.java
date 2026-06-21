// service/AuthService.java - Updated response messages
package com.example.aiapp.aiapi.service;

import com.example.aiapp.aiapi.dto.requests.*;
import com.example.aiapp.aiapi.dto.response.AuthResponse;
import com.example.aiapp.aiapi.entity.User;
import com.example.aiapp.aiapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableAsync
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.reset.token.expiry.minutes:15}")
    private int resetTokenExpiryMinutes;

    @Value("${app.max.reset.attempts:5}")
    private int maxResetAttempts;

    // ==================== REGISTRATION ====================

    @Transactional
    public String register(RegisterRequest request) {
        log.info("REGISTER START");
        String emailLower = request.getEmail().toLowerCase().trim();
        log.info("emilLower:"+emailLower);

        // Check if email already exists
        if (userRepository.existsByEmail(emailLower)) {
            log.warn("Registration attempt with existing email: {}", emailLower);
            return "Email already registered. Please login or use a different email.";
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
        log.info("USER SAVED");

        log.info("OTP GENERATED: {}", otp);

        log.info("CALLING sendOtpEmailAsync");

        sendOtpEmailAsync(user.getEmail(), otp);

        log.info("sendOtpEmailAsync CALL COMPLETED");

        return "Registration successful. Please check your email for OTP verification.";
    }

    @Async
    public void sendOtpEmailAsync(String email, String otp) {

        log.info("========== ASYNC METHOD START ==========");
        log.info("Email: {}", email);

        try {

            log.info("Calling EmailService");

            emailService.sendOtpEmail(email, otp);

            log.info("EmailService completed successfully");

        } catch (Exception e) {

            log.error("ASYNC ERROR", e);

        }

        log.info("========== ASYNC METHOD END ==========");
    }

    // ==================== EMAIL VERIFICATION ====================

    @Transactional
    public String verifyEmail(VerifyEmailRequest request) {
        String emailLower = request.getEmail().toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(emailLower);

        if (userOpt.isEmpty()) {
            return "User not found. Please register first.";
        }

        User user = userOpt.get();

        if (user.isEmailVerified()) {
            return "Email already verified. You can now login.";
        }

        if (user.getVerificationOtp() == null) {
            return "No OTP found. Please request a new OTP.";
        }

        if (!user.getVerificationOtp().equals(request.getOtp())) {
            return "Invalid OTP. Please check and try again.";
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            return "OTP has expired. Please request a new OTP.";
        }

        user.setEmailVerified(true);
        user.setVerificationOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);

        return "Email verified successfully. You can now login.";
    }

    // ==================== RESEND OTP ====================

    @Transactional
    public String resendOtp(ResendOtpRequest request) {
        String emailLower = request.getEmail().toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(emailLower);

        if (userOpt.isEmpty()) {
            return "User not found. Please register first.";
        }

        User user = userOpt.get();

        if (user.isEmailVerified()) {
            return "Email already verified. Please login.";
        }

        String newOtp = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationOtp(newOtp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        sendOtpEmailAsync(user.getEmail(), newOtp);

        return "OTP resent successfully. Please check your email.";
    }

    // ==================== LOGIN ====================

    public AuthResponse login(LoginRequest request) {
        String emailLower = request.getEmail().toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(emailLower);

        if (userOpt.isEmpty()) {
            return new AuthResponse(null, "Invalid email or password");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse(null, "Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            return new AuthResponse(null, "Please verify your email first. Check your inbox for OTP.");
        }

        userRepository.updateLastLogin(emailLower, LocalDateTime.now());

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token, "Login successful");
    }

    // ==================== FORGOT PASSWORD ====================

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        String emailLower = request.getEmail().toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(emailLower);

        if (userOpt.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", emailLower);
            return "Email not registered. Please create an account first.";
        }

        User user = userOpt.get();

        if (user.getPasswordResetAttempts() != null &&
                user.getPasswordResetAttempts() >= maxResetAttempts) {
            return "Too many reset attempts. Please try again after 24 hours.";
        }

        String resetToken = generateSecureToken();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(resetTokenExpiryMinutes);

        userRepository.updateResetToken(emailLower, resetToken, expiryTime, LocalDateTime.now());
        userRepository.incrementResetAttempts(emailLower);

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

        return "Password reset link sent to your email. Please check your inbox.";
    }

    // ==================== RESET PASSWORD ====================

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return "Passwords do not match.";
        }

        if (!isValidPassword(request.getNewPassword())) {
            return "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.";
        }

        Optional<User> userOpt = userRepository.findByResetToken(request.getToken());

        if (userOpt.isEmpty()) {
            return "Invalid or expired reset token.";
        }

        User user = userOpt.get();

        if (LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return "Reset token has expired. Please request a new one.";
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return "New password must be different from your current password.";
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.setLastPasswordReset(LocalDateTime.now());
        user.setPasswordResetAttempts(0);
        userRepository.save(user);

        userRepository.resetResetAttempts(user.getId());

        return "Password reset successful. You can now login with your new password.";
    }

    //VALIDATE TOKEN ====================

    @Transactional
    public String validateResetToken(String token) {
        Optional<User> userOpt = userRepository.findByResetToken(token);

        if (userOpt.isEmpty()) {
            return "Invalid reset token.";
        }

        User user = userOpt.get();

        if (LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return "Reset token has expired. Please request a new one.";
        }

        return "Token is valid.";
    }

    // PRIVATE METHODS

    private String generateSecureToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private boolean isValidPassword(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }
}