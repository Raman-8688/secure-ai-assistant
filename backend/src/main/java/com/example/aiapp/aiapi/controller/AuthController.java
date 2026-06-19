package com.example.aiapp.aiapi.controller;

import com.example.aiapp.aiapi.dto.*;
import com.example.aiapp.aiapi.entity.User;
import com.example.aiapp.aiapi.repository.UserRepository;
import com.example.aiapp.aiapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        String result = authService.register(request);
        Map<String, String> response = new HashMap<>();

        if (result.contains("successful")) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        String result = authService.verifyEmail(request);
        Map<String, String> response = new HashMap<>();

        if (result.contains("successfully")) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        Map<String, Object> result = new HashMap<>();

        if (response.getToken() != null) {
            result.put("token", response.getToken());
            result.put("message", response.getMessage());
            return ResponseEntity.ok(result);
        } else {
            result.put("error", response.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, String>> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        String result = authService.resendOtp(request);
        Map<String, String> response = new HashMap<>();

        if (result.contains("success")) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        user.setPassword(null);
        user.setVerificationOtp(null);

        return ResponseEntity.ok(user);
    }



    // controller/AuthController.java

    @PostMapping("/forgot-password")
    public ResponseEntity<ResetPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        ResetPasswordResponse response = authService.forgotPassword(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // Check different error types
            if ("email_not_found".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if ("too_many_attempts".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        ResetPasswordResponse response = authService.resetPassword(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<ResetPasswordResponse> validateResetToken(
            @RequestParam String token) {

        ResetPasswordResponse response = authService.validateResetToken(token);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}