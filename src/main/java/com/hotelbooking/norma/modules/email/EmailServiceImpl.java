package com.hotelbooking.norma.modules.email;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hotelbooking.norma.modules.email.dto.SendEmailRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    // @RabbitListener(queues = {RabbitMQConfig.EMAIL_QUEUE})
    public void consumeAndSendEmail(SendEmailRequest request) {
        log.info("Received email request from queue for recipient: {}", request.getRecipient());
        
        // This method will now trigger the robust, async, and retryable email sending logic.
        sendTemplatedEmail(request);
    }
    
    @Override
    public String appendPlaceholders(String templateName, Map<String, String> placeholders) {
        String emailContent = getEmailTemplate(templateName);
        if (emailContent == null) {
            throw new RuntimeException("Email template not found: " + templateName);
        }

        // Replace placeholders dynamically
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            emailContent = emailContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return emailContent;
    }

    @Override
    public String getEmailTemplate(String templateName) {
        String filePath = "templates/email/" + templateName + ".html";

        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading email template: " + filePath, e);
        }catch (MailException e) {
            log.error("Email sending failed: {}", e.getMessage());
            throw new RuntimeException("Failed to send email", e); // REQUIRED to trigger rollback
        }
    }

    @Async
    @Retryable(
        value = {MailException.class, MessagingException.class}, // Retry only for these exceptions
        maxAttempts = 3, // Try 3 times before failing
        backoff = @Backoff(delay = 2000) // Wait 2 seconds before retrying
    )
    @Override
    public void sendEmail(String recipient, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true); // true means HTML email support

            mailSender.send(message);
            log.info("Email sent successfully to {}", recipient);

        } catch (MessagingException | MailException e) {
            log.error("Email sending error occurred: {}", e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendTemplatedEmail(SendEmailRequest request) {
        String htmlContent = appendPlaceholders(request.getTemplateName(), request.getPlaceholders());
        sendEmail(request.getRecipient(), request.getSubject(), htmlContent);
    }
}
