package com.example.aiapp.aiapi.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class SendGridEmailService {

    @Value("${sendgrid.api.key:}")
    private String sendgridApiKey;

    @Value("${sendgrid.from.email:}")
    private String fromEmail;

    @Value("${sendgrid.from.name:AI Assistant}")
    private String fromName;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    private SendGrid sendGrid;

    @PostConstruct
    public void init() {
        log.info("====================================");
        log.info("SendGrid Service Initialization");
        log.info("API Key configured: {}", sendgridApiKey != null && !sendgridApiKey.isEmpty());
        log.info("From Email: {}", fromEmail);
        log.info("Frontend URL: {}", frontendUrl);
        log.info("====================================");

        if (sendgridApiKey != null && !sendgridApiKey.isEmpty()) {
            this.sendGrid = new SendGrid(sendgridApiKey);
            log.info("SendGrid initialized successfully");
        } else {
            log.warn("SendGrid API Key is not configured - emails will fail");
        }
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            log.info("Sending OTP via SendGrid to: {}", toEmail);

            // Verify configuration
            if (sendGrid == null || fromEmail == null || fromEmail.isEmpty()) {
                log.error("SendGrid not properly configured. Cannot send email.");
                throw new RuntimeException("SendGrid not configured properly");
            }

            Email from = new Email(fromEmail, fromName);
            String subject = "AI Assistant - Verify Your Email";
            Email to = new Email(toEmail);

            String contentText = String.format(
                    "Hello,\n\n" +
                            "Thank you for registering with AI Assistant!\n\n" +
                            "Your OTP for email verification is: %s\n\n" +
                            "This OTP is valid for 10 minutes.\n\n" +
                            "If you didn't request this, please ignore this email.\n\n" +
                            "Best regards,\nAI Assistant Team",
                    otp
            );

            Content content = new Content("text/plain", contentText);
            Mail mail = new Mail(from, subject, to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("OTP email sent successfully via SendGrid to: {}, Status: {}",
                        toEmail, response.getStatusCode());
            } else {
                log.error("SendGrid failed with status: {}, body: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("SendGrid email failed with status: " + response.getStatusCode());
            }

        } catch (IOException e) {
            log.error("Failed to send OTP email via SendGrid to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            log.info("Sending password reset via SendGrid to: {}", toEmail);
            log.info("Reset link: {}", resetLink);

            // Verify configuration
            if (sendGrid == null || fromEmail == null || fromEmail.isEmpty()) {
                log.error("SendGrid not properly configured. Cannot send email.");
                throw new RuntimeException("SendGrid not configured properly");
            }

            Email from = new Email(fromEmail, fromName);
            String subject = "AI Assistant - Password Reset Request";
            Email to = new Email(toEmail);

            String contentText = String.format(
                    "Hello,\n\n" +
                            "We received a request to reset your password for your AI Assistant account.\n\n" +
                            "To reset your password, click the link below:\n" +
                            "%s\n\n" +
                            "This link will expire in 15 minutes.\n\n" +
                            "If you did not request a password reset, please ignore this email or contact support if you have concerns.\n\n" +
                            "Best regards,\nAI Assistant Team",
                    resetLink
            );

            Content content = new Content("text/plain", contentText);
            Mail mail = new Mail(from, subject, to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Password reset email sent successfully via SendGrid to: {}, Status: {}",
                        toEmail, response.getStatusCode());
            } else {
                log.error("SendGrid failed with status: {}, body: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("SendGrid email failed with status: " + response.getStatusCode());
            }

        } catch (IOException e) {
            log.error("Failed to send password reset email via SendGrid to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage(), e);
        }
    }

    public boolean isConfigured() {
        return sendGrid != null && fromEmail != null && !fromEmail.isEmpty();
    }
}