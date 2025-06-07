package com.hotelbooking.norma.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hotelbooking.norma.email.dto.EmailMessageDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    // @RabbitListener(queues = "${rabbitmq.email.queue}")
    public void sendEmail(EmailMessageDto emailMessage) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(emailMessage.getTo());
            mailMessage.setSubject(emailMessage.getSubject());
            mailMessage.setText(emailMessage.getBody());

            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
