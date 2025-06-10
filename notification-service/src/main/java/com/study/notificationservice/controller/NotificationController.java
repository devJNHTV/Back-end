package com.study.notificationservice.controller;

import com.study.comonlibrary.dto.NotificationEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationEvent request) {
         log.info("Received notification request by Webclient from Auth-Service: {}", request.toString());
         //call service send email, or sms  to user
        return ResponseEntity.ok("Notification sent to: " + request.getRecipient());
    }

}