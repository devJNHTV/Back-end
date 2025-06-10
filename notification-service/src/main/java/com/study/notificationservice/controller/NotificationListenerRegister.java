package com.study.notificationservice.controller;

import com.study.comonlibrary.dto.NotificationEvent;
import com.study.notificationservice.dto.request.Recipient;
import com.study.notificationservice.dto.request.SendEmailRequest;
import com.study.notificationservice.service.EmailService;
import com.study.notificationservice.service.EmailServicebyMailSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;


@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationListenerRegister {
    EmailService    emailService;
    EmailServicebyMailSender emailServicebyMailSender;
    @KafkaListener(topics = "send-email-welcome")
    public void listenCreateSuccessful(@Payload NotificationEvent message) {

       log.info("Message recevied : " +message);
        emailServicebyMailSender.sendEmail(message.getRecipient(), message.getSubject(), message.getBody());


    }
}
