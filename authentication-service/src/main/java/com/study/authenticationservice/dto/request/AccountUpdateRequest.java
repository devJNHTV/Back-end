package com.study.authenticationservice.dto.request;

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
public class AccountUpdateRequest {
    @NotBlank(message = "account.password.blank")
    @Size(min = 6, message = "account.password.invalid")
    String password;


    @NotBlank(message = "account.firstName.blank")
    String firstName;

    @NotBlank(message = "account.lastName.blank")
    String lastName;

    @Schema(type = "string", format = "binary")
    MultipartFile image;

    @NotBlank(message = "account.email.blank")
    @Email(message = "account.email.invalid")
    @Size(max = 255, message = "account.email.invalid") // Hoặc thêm @Email nếu cần kiểm tra đúng định dạng
    String email;
}
