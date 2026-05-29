package com.example.aiapp.aiapi.dto;



/*
 * Common response after login/register.
 *
 * Backend sends token and message.
 */
public class AuthResponse {

    private String token;
    private String message;

    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
