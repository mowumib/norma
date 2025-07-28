package com.hotelbooking.norma.email;

import java.util.Map;

import com.hotelbooking.norma.email.dto.SendEmailRequest;

public interface EmailService {
    String appendPlaceholders(String templateName, Map<String, String> placeholders);

    String getEmailTemplate(String templateName);

    void sendEmail(String recipient, String subject, String body);

    public void sendTemplatedEmail(SendEmailRequest request);
}
