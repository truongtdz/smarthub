package com.smarthub.smarthub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("SmartHub <SmartHub>");
            message.setTo(toEmail);
            message.setSubject("Xác nhận đơn hàng");
            message.setText(content);

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}