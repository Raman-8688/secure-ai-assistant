package com.example.aiapp.aiapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;



@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public void sendOtpEmail(String toEmail, String otp) {
        log.info("Sending OTP email to: {}", toEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("AI Assistant - Your OTP Code");
            message.setText(String.format(
                    "Hello,\n\n" +
                            "Your OTP for email verification is: %s\n\n" +
                            "This OTP is valid for 10 minutes.\n\n" +
                            "If you didn't request this, please ignore this email.\n\n" +
                            "Best regards,\nAI Assistant Team",
                    otp
            ));

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            log.info("=========================================");
            log.info("⚠️ EMAIL FAILED - Use this OTP for testing: {}", otp);
            log.info("=========================================");
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        log.info("Sending password reset email to: {}", toEmail);

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
            log.info("Password reset email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            log.info("=========================================");
            log.info("⚠️ EMAIL FAILED - Use this token for testing: {}", resetToken);
            log.info("Reset Link: {}?token={}", frontendUrl, resetToken);
            log.info("=========================================");
        }
    }
}