package com.study.profile.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String profileId; // Đảm bảo bạn sử dụng đúng @Id từ jakarta.persistence

    // UserId from Keycloak
    String userId;
    String email;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
}
