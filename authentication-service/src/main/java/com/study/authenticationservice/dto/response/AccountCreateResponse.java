package com.study.authenticationservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountCreateResponse  implements Serializable {
    private static final long serialVersionUID = 1L;
    String id;
    String userId;
    String username;
    String firstName;
    String lastName;
    String email;
    String image;
}
