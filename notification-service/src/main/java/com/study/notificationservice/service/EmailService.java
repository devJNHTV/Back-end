package com.study.notificationservice.service;

import com.study.notificationservice.dto.request.EmailRequest;
import com.study.notificationservice.dto.request.SendEmailRequest;
import com.study.notificationservice.dto.request.Sender;
import com.study.notificationservice.dto.response.EmailResponse;
import com.study.notificationservice.repository.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;

    @Value("${notification.email.brevo-apikey}")
    @NonFinal
    String apiKey;

    public EmailResponse sendEmail(SendEmailRequest request) {
        log.info("api key : " +apiKey);
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("vy nguyen")
                        .email("vythanhan9984@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}