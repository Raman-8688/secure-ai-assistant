package com.example.aiapp.aiapi.config;

import com.example.aiapp.aiapi.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/*
 * JwtAuthenticationFilter runs for every request.
 *
 * Responsibilities:
 *
 * 1. Read Authorization header
 * 2. Extract JWT token
 * 3. Validate token
 * 4. Extract user email
 * 5. Authenticate user in Spring Security
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService
    ) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * Read Authorization header.
         *
         * Example:
         * Authorization: Bearer eyJhbGciOi...
         */
        final String authHeader =
                request.getHeader("Authorization");

        /*
         * If token missing, continue request.
         */
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Remove "Bearer ".
         */
//        String token =
//                authHeader.substring(7);
//
//        /*
//         * Validate token.
//         */
//        if (!jwtService.isTokenValid(token)) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//        System.out.println("Request URL: " + request.getRequestURI());
//        System.out.println("Auth Header: " + authHeader);

        String token = authHeader.substring(7);

//        System.out.println("Token: " + token);
//        System.out.println("Token valid: " + jwtService.isTokenValid(token));

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.extractEmail(token);
//        System.out.println("Email from token: " + email);
        /*
         * Extract user email.
         */
//        String email =
//                jwtService.extractEmail(token);

        /*
         * Create Spring Security user object.
         */
        UserDetails userDetails =
                User.withUsername(email)
                        .password("")
                        .authorities(Collections.emptyList())
                        .build();

        /*
         * Create authentication token.
         */
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        /*
         * Set authenticated user into security context.
         */
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        /*
         * Continue request.
         */
        filterChain.doFilter(request, response);
    }
}