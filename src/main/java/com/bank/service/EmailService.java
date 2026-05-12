package com.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;                                                                  // Concept: Field | Why: stores persistent state required by this type.

    @Value("${app.mail.from:no-reply@bank.local}")
    private String fromEmail;                                                                                 // Concept: Field | Why: keeps contact address for notifications and verification.

    @Value("${app.base-url:http:                                                                              // Concept: Note | Why: localhost:8081}")
    private String appBaseUrl;                                                                                // Concept: Field | Why: stores persistent state required by this type.

    public void sendVerificationEmail(String username, String toEmail, String token) {                        // Concept: Method | Why: encapsulates sendVerificationEmail behavior for this component boundary.
        String verifyUrl = appBaseUrl + "/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify your account");
        message.setText("Hi " + username + ",\n\nPlease verify your account by clicking this link:\n" + verifyUrl
                + "\n\nThis link expires in 24 hours.");
        mailSender.send(message);
    }
}
