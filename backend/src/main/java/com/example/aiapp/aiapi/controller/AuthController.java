// controller/AuthController.java
package com.example.aiapp.aiapi.controller;

import com.example.aiapp.aiapi.dto.requests.*;
import com.example.aiapp.aiapi.dto.response.AuthResponse;
import com.example.aiapp.aiapi.dto.response.ResetPasswordResponse;
import com.example.aiapp.aiapi.entity.User;
import com.example.aiapp.aiapi.repository.UserRepository;
import com.example.aiapp.aiapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        String result = authService.register(request);
        Map<String, String> response = new HashMap<>();

        if (result.contains("successful")) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            // If email already exists, send proper message
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

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String result = authService.forgotPassword(request);
        Map<String, String> response = new HashMap<>();

        if (result.contains("sent") || result.contains("success")) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            if (result.contains("not registered")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (result.contains("Too many")) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
            }
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String result = authService.resetPassword(request);
        Map<String, String> response = new HashMap<>();

        if (result.contains("successful")) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<Map<String, String>> validateResetToken(@RequestParam String token) {
        String result = authService.validateResetToken(token);
        Map<String, String> response = new HashMap<>();

        if (result.contains("valid")) {
            response.put("message", result);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }
}