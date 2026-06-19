// dto/ResetPasswordResponse.java
package com.example.aiapp.aiapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordResponse {
    private String message;
    private boolean success;
    private String status; // "success", "email_not_found", "too_many_attempts", "error"

    // Constructor for backward compatibility (2 params)
    public ResetPasswordResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.status = success ? "success" : "error";
    }
}