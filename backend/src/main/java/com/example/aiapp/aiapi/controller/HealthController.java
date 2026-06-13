package com.example.aiapp.aiapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class HealthController {

    @Autowired(required = false)
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("environment", System.getenv("SPRING_PROFILES_ACTIVE"));

        // Check database connection
        boolean dbConnected = checkDatabase();
        health.put("database", dbConnected ? "CONNECTED" : "DISCONNECTED");

        // Check required environment variables
        Map<String, Boolean> configStatus = checkConfigurations();
        health.put("configurations", configStatus);

        return ResponseEntity.ok(health);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Backend is working!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("activeProfile", System.getenv("SPRING_PROFILES_ACTIVE"));
        return ResponseEntity.ok(response);
    }

    private boolean checkDatabase() {
        try {
            if (dataSource != null) {
                try (Connection conn = dataSource.getConnection()) {
                    return conn.isValid(2);
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Boolean> checkConfigurations() {
        Map<String, Boolean> configs = new HashMap<>();
        configs.put("GOOGLE_CLIENT_ID", System.getenv("GOOGLE_CLIENT_ID") != null);
        configs.put("GITHUB_CLIENT_ID", System.getenv("GITHUB_CLIENT_ID") != null);
        configs.put("JWT_SECRET", System.getenv("JWT_SECRET") != null);
        configs.put("DATABASE", System.getenv("DB_URL") != null);
        configs.put("MAIL_CONFIGURED", System.getenv("MAIL_USERNAME") != null);
        return configs;
    }
}