package com.study.comonlibrary.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent
{
    String channel;
    String recipient;
    String subject;
    String body;
}
