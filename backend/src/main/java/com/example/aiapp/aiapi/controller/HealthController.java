package com.example.aiapp.aiapi.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/actuator/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return status;
    }

    @GetMapping("/api/public/health")
    public Map<String, String> publicHealth() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "OK");
        status.put("message", "Backend is running");
        return status;
    }
}
