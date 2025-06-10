package com.study.authenticationservice.handler;

import com.study.authenticationservice.dto.request.AccountCreateRequest;
import com.study.authenticationservice.dto.response.AccountCreateResponse;
import com.study.authenticationservice.exception.AppException;
import com.study.authenticationservice.exception.ErrorCode;
import com.study.authenticationservice.repository.AccountDAO;
import com.study.authenticationservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountHandler {

    private final AccountService accountService;
    private final AccountDAO accountDAO;

    public AccountCreateResponse handleCreateAccount(AccountCreateRequest request) throws Exception {
        if (accountDAO.existsByUsername(request.getUsername())) {
            log.error("Username already exists: {}", request.getUsername());
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.error("Password confirmation failed for username: {}", request.getUsername());
            throw new AppException(ErrorCode.INVALID_CONFIRMPASSWORD);
        }
        if (accountDAO.existsByEmail(request.getEmail()))
        {
            log.error("Email already exists: {}", request.getEmail());
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        return accountService.createAccount(request);
    }
}