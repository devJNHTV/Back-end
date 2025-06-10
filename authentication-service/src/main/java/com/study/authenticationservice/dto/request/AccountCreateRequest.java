package com.study.authenticationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountCreateRequest {
    @NotBlank(message = "account.username.blank")
    @Size(min = 4, max = 20, message = "account.username.invalid")
    String username;

    @NotBlank(message = "account.password.blank")
    @Size(min = 6, message = "account.password.invalid")
    String password;

    @NotBlank(message="account.confirmpassword.blank")
    @Size(min = 6, message = "account.confirmpassword.invalid")
    String confirmPassword;

    @NotBlank(message = "account.firstName.blank")
    String firstName;

    @NotBlank(message = "account.lastName.blank")
    String lastName;
    @Schema(type = "string", format = "binary")
    MultipartFile image;

    @NotBlank(message = "account.email.blank")
    @Email(message = "account.email.invalid")
    String email;
}
