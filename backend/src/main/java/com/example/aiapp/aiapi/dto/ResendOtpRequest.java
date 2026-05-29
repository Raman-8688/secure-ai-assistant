package com.example.aiapp.aiapi.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * Used when user clicks:
 * "Resend OTP"
 */
public class ResendOtpRequest {

    @Email
    @NotBlank
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
