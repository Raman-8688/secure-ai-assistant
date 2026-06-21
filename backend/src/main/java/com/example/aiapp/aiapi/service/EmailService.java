// service/EmailService.java
package com.example.aiapp.aiapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;



import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
public class EmailService {


    @Value("${spring.mail.username:NOT_FOUND}")
    private String mailUsername;

    @Value("${spring.mail.host:NOT_FOUND}")
    private String mailHost;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Send OTP email for email verification
     */
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            log.info("========== EMAIL SEND START ==========");
            log.info("Sending OTP to: {}", toEmail);
            log.info("Configured Mail Username: {}", mailUsername);

            long startTime = System.currentTimeMillis();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("AI Assistant - Verify Your Email");
            message.setText(String.format(
                    "Hello,\n\n" +
                            "Thank you for registering with AI Assistant!\n\n" +
                            "Your OTP for email verification is: %s\n\n" +
                            "This OTP is valid for 10 minutes.\n\n" +
                            "If you didn't request this, please ignore this email.\n\n" +
                            "Best regards,\nAI Assistant Team",
                    otp
            ));

            mailSender.send(message);
            long endTime = System.currentTimeMillis();

            log.info("mailSender.send() completed");
            log.info("Time Taken: {} ms", (endTime - startTime));

            log.info("OTP email sent successfully to {}", toEmail);
            log.info("========== EMAIL SEND END ==========");
            log.info("OTP email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            log.info("=========================================");
            log.info("EMAIL FAILED - Use this OTP for testing: {}", otp);
            log.info("=========================================");

            log.error("FULL EMAIL ERROR", e);
        }
    }

    /**
     * Send password reset email with reset link
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("AI Assistant - Password Reset Request");
            message.setText(String.format(
                    "Hello,\n\n" +
                            "We received a request to reset your password for your AI Assistant account.\n\n" +
                            "To reset your password, click the link below:\n" +
                            "%s\n\n" +
                            "This link will expire in 15 minutes.\n\n" +
                            "If you did not request a password reset, please ignore this email or contact support if you have concerns.\n\n" +
                            "Best regards,\nAI Assistant Team",
                    resetLink
            ));

            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            log.info("=========================================");
            log.info("EMAIL FAILED - Use this token for testing: {}", resetToken);
            log.info("Reset Link: {}?token={}", frontendUrl, resetToken);
            log.info("=========================================");
        }
    }



    @PostConstruct
    public void verifyMailConfiguration() {

        log.info("====================================");
        log.info("MAIL CONFIGURATION CHECK");
        log.info("****Mail Host: {}", mailHost);
        log.info("****Mail Username: {}", mailUsername);

        String envUsername = System.getenv("MAIL_USERNAME");

        if (envUsername != null) {
            log.info("ENV MAIL_USERNAME loaded successfully");
        } else {
            log.error("ENV MAIL_USERNAME IS NULL");
        }

        log.info("====================================");
    }



}