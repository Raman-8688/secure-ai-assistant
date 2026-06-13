package com.example.aiapp.aiapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Value("${spring.security.oauth2.client.registration.github.client-id:NOT_SET}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret:NOT_SET}")
    private String githubClientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri:NOT_SET}")
    private String githubRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id:NOT_SET}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret:NOT_SET}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri:NOT_SET}")
    private String googleRedirectUri;

    @GetMapping("/oauth-config")
    public Map<String, Object> getOAuthConfig() {
        Map<String, Object> config = new HashMap<>();

        // GitHub config
        config.put("github.clientId", maskString(githubClientId));
        config.put("github.clientSecret", githubClientSecret.equals("NOT_SET") ? "NOT_SET" : "SET (length: " + githubClientSecret.length() + ")");
        config.put("github.redirectUri", githubRedirectUri);

        // Google config
        config.put("google.clientId", maskString(googleClientId));
        config.put("google.clientSecret", googleClientSecret.equals("NOT_SET") ? "NOT_SET" : "SET (length: " + googleClientSecret.length() + ")");
        config.put("google.redirectUri", googleRedirectUri);

        // Show which environment variables are actually set in the system
        config.put("systemEnv.GITHUB_CLIENT_ID", maskString(System.getenv("GITHUB_CLIENT_ID")));
        config.put("systemEnv.GOOGLE_CLIENT_ID", maskString(System.getenv("GOOGLE_CLIENT_ID")));

        return config;
    }

    private String maskString(String value) {
        if (value == null || value.equals("NOT_SET")) return "NOT_SET";
        if (value.length() <= 8) return "***";
        return value.substring(0, 4) + "..." + value.substring(value.length() - 4);
    }
}