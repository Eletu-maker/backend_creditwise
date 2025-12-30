package com.creditwise.service.impl;

import com.creditwise.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String to, String otpCode) {
        String subject = "Your OTP Code for Admin Login";
        String text = String.format(
            "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 10px;'>\n" +
            "    <h2 style='color: #333; text-align: center;'>Admin Login OTP</h2>\n" +
            "    <p>Hello,</p>\n" +
            "    <p>You have requested to login to the admin panel. Please use the following OTP to complete your login:</p>\n" +
            "    <div style='text-align: center; margin: 30px 0;'>\n" +
            "        <span style='display: inline-block; padding: 15px 30px; font-size: 24px; font-weight: bold; background-color: #f0f8ff; border: 2px dashed #007bff; border-radius: 8px; letter-spacing: 3px;'>%s</span>\n" +
            "    </div>\n" +
            "    <p>This OTP is valid for 5 minutes only. If you did not request this, please ignore this email.</p>\n" +
            "    <p>Best regards,<br>The CreditWise Team</p>\n" +
            "</div>", 
            otpCode
        );

        sendEmail(to, subject, text);
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // Enable HTML content
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}