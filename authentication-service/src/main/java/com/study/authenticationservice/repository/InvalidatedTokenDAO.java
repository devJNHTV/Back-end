package com.study.authenticationservice.repository;

import com.study.authenticationservice.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenDAO extends JpaRepository<InvalidatedToken, String> {
}
