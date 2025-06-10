package com.study.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Accounts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends Auditable implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String userId;
    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String image;
}
