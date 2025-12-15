package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from:noreply@openprofile.ru}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Async
    public void sendVerificationEmail(String to, String token) {
        String subject = "Verify your email - OpenProfile";
        String verificationUrl = baseUrl + "/api/auth/verify-email?token=" + token;
        String message = String.format(
                "Please click the link below to verify your email:\n\n%s\n\nThis link will expire in 24 hours.",
                verificationUrl
        );

        sendEmail(to, subject, message);
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Reset your password - OpenProfile";
        String resetUrl = baseUrl;
        String message = String.format(
                "You requested a password reset. Click the link below to reset your password:\n\n%s\n\nThis link will expire in 1 hour.\n\nIf you didn't request this, please ignore this email.",
                resetUrl
        );

        sendEmail(to, subject, message);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
}