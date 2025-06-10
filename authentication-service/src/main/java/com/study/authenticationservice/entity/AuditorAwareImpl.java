package com.study.authenticationservice.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra nếu người dùng chưa đăng nhập (authentication là null hoặc không có tên)
        if (auth == null || auth.getName() == null) {
            log.info("No user is authenticated, returning anonymous");
            return Optional.of("anonymousUser");
        }

        log.info("Current Auditor: " + auth.getName());
        return Optional.of(auth.getName());
    }
}