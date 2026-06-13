package com.example.aiapp.aiapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuthConfigChecker {

    public OAuthConfigChecker(
            @Value("${spring.security.oauth2.client.registration.github.client-id:NOT_SET}") String githubClientId,
            @Value("${spring.security.oauth2.client.registration.github.redirect-uri:NOT_SET}") String githubRedirectUri,
            @Value("${spring.security.oauth2.client.registration.google.client-id:NOT_SET}") String googleClientId,
            @Value("${spring.security.oauth2.client.registration.google.redirect-uri:NOT_SET}") String googleRedirectUri
    ) {
        System.out.println("=== OAuth Configuration Check ===");
        System.out.println("GitHub Client ID: " + (githubClientId.equals("NOT_SET") ? "❌ NOT SET" : "✅ " + githubClientId.substring(0, Math.min(10, githubClientId.length())) + "..."));
        System.out.println("GitHub Redirect URI: " + (githubRedirectUri.equals("NOT_SET") ? "❌ NOT SET" : "✅ " + githubRedirectUri));
        System.out.println("Google Client ID: " + (googleClientId.equals("NOT_SET") ? "❌ NOT SET" : "✅ " + googleClientId.substring(0, Math.min(10, googleClientId.length())) + "..."));
        System.out.println("Google Redirect URI: " + (googleRedirectUri.equals("NOT_SET") ? "❌ NOT SET" : "✅ " + googleRedirectUri));
        System.out.println("=================================");

        // Also check system environment variables
        System.out.println("System Env - GITHUB_CLIENT_ID: " + (System.getenv("GITHUB_CLIENT_ID") != null ? "✅ SET" : "❌ NOT SET"));
        System.out.println("System Env - GITHUB_CLIENT_SECRET: " + (System.getenv("GITHUB_CLIENT_SECRET") != null ? "✅ SET" : "❌ NOT SET"));
        System.out.println("System Env - GOOGLE_CLIENT_ID: " + (System.getenv("GOOGLE_CLIENT_ID") != null ? "✅ SET" : "❌ NOT SET"));
        System.out.println("System Env - GOOGLE_CLIENT_SECRET: " + (System.getenv("GOOGLE_CLIENT_SECRET") != null ? "✅ SET" : "❌ NOT SET"));
    }
}