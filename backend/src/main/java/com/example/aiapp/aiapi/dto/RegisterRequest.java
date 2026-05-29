package com.example.aiapp.aiapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * Registration request coming from frontend.
 *
 * Example:
 * {
 *   "name": "Raman",
 *   "email": "raman@gmail.com",
 *   "password": "Password@123"
 * }
 */
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    /*
     * Password should have minimum 6 characters.
     */
    @Size(min = 6, message = "Password must contain minimum 6 characters")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
