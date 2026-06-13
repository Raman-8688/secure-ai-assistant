package com.example.aiapp.aiapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "pong");
        response.put("status", "alive");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", payload);
        response.put("echo", "Request received successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth-check")
    public ResponseEntity<Map<String, String>> checkAuthEndpoints() {
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("register", "/api/auth/register");
        endpoints.put("login", "/api/auth/login");
        endpoints.put("google-oauth", "/oauth2/authorization/google");
        endpoints.put("github-oauth", "/oauth2/authorization/github");
        endpoints.put("verify-email", "/api/auth/verify-email");
        return ResponseEntity.ok(endpoints);
    }

//    http://localhost:8080/swagger-ui.html
}