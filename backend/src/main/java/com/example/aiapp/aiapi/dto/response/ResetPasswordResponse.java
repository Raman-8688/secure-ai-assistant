// dto/response/ResetPasswordResponse.java
package com.example.aiapp.aiapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordResponse {
    private String message;
    private boolean success;
    private String status;

    // Constructor for backward compatibility
    public ResetPasswordResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.status = success ? "success" : "error";
    }
}