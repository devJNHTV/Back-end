package com.study.authenticationservice.temporal.activities;

import com.study.authenticationservice.dto.request.AccountCreateRequest;
import com.study.authenticationservice.dto.response.AccountCreateResponse;
import com.study.authenticationservice.entity.Account;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GetUserByIdActivities {
    @ActivityMethod
    AccountCreateResponse GetUserByID(String id);
}
