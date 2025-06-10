package com.study.authenticationservice.repository;

import com.study.authenticationservice.entity.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
public interface AccountDAO extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Object> findByUserId(String userId);

    boolean existsByEmail(String email);}
