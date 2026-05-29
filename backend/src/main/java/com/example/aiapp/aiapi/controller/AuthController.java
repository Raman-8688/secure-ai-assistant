package com.example.aiapp.aiapi.controller;

import com.example.aiapp.aiapi.dto.*;

import com.example.aiapp.aiapi.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 * AuthController exposes authentication APIs.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /*
     * User registration API.
     */
    @PostMapping("/register")
    public String register(
            @Valid @RequestBody RegisterRequest request
    ) {

        return authService.register(request);
    }

    /*
     * Verify OTP API.
     */
    @PostMapping("/verify-email")
    public String verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {

        return authService.verifyEmail(request);
    }

    /*
     * Login API.
     */
    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ) {

        return authService.login(request);
    }

    /*
     * Resend OTP API.
     */
    @PostMapping("/resend-otp")
    public String resendOtp(
            @Valid @RequestBody ResendOtpRequest request
    ) {

        return authService.resendOtp(request);
    }
}