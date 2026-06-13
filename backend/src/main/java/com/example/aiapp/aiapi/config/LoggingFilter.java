package com.example.aiapp.aiapi.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Wrap request and response to read body multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        // Log request
        log.info("=== INCOMING REQUEST ===");
        log.info("Method: {}", httpRequest.getMethod());
        log.info("URI: {}", httpRequest.getRequestURI());
        log.info("Query String: {}", httpRequest.getQueryString());
        log.info("Headers: {}", Collections.list(httpRequest.getHeaderNames()).stream()
                .map(header -> header + ": " + httpRequest.getHeader(header))
                .collect(Collectors.joining(", ")));
        log.info("Client IP: {}", httpRequest.getRemoteAddr());

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Log response
            log.info("=== RESPONSE ===");
            log.info("Status: {}", httpResponse.getStatus());
            log.info("Duration: {} ms", duration);

            // Log response body for errors
            if (httpResponse.getStatus() >= 400) {
                byte[] responseBody = wrappedResponse.getContentAsByteArray();
                if (responseBody.length > 0) {
                    log.error("Error Response Body: {}", new String(responseBody));
                }
            }

            wrappedResponse.copyBodyToResponse();
            log.info("=== REQUEST COMPLETED ===\n");
        }
    }
}