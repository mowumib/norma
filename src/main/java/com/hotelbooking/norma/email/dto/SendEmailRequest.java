package com.hotelbooking.norma.email.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRequest {
    private String recipient;
    private String subject;
    private String templateName;
    private Map<String, String> placeholders;
}
