package com.study.authenticationservice.temporal.activities.Implement;

import com.study.authenticationservice.dto.response.AccountCreateResponse;
import com.study.authenticationservice.mapper.AccountMapper;
import com.study.authenticationservice.repository.AccountDAO;
import com.study.authenticationservice.temporal.activities.GetUserByIdActivities;

import io.temporal.failure.ApplicationFailure;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetUserByIDActivitiesImpl implements GetUserByIdActivities {
    AccountDAO accountDAO;
    AccountMapper accountMapper;


    @Override
    public AccountCreateResponse GetUserByID(String id) {
        return accountDAO.findById(id)
                .map(accountMapper::toAccountCreateResponse)
                .orElseThrow(() -> {
                    log.warn("Account not found with ID: {}", id);
                    // NÉM LỖI ĐÚNG CÁCH để workflow bắt được
                    return ApplicationFailure.newNonRetryableFailure("USER_NOT_FOUND", "UserNotFoundError");
                });
    }
}
