package com.example.aiapp.aiapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/*
 * EmailService sends OTP verification mail.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /*
     * Send OTP mail to user.
     */
    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject("AI Assistant Email Verification OTP");

        message.setText(
                "Hello,\n\n" +
                        "Your OTP for AI Assistant verification is: " + otp +
                        "\n\nThis OTP expires in 10 minutes."
        );

        mailSender.send(message);
    }
}