package com.study.authenticationservice.service;

import com.study.authenticationservice.dto.request.AccountCreateRequest;
import com.study.authenticationservice.dto.request.AccountUpdateRequest;
import com.study.authenticationservice.dto.response.AccountCreateResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface AccountService  {


    AccountCreateResponse createAccount(AccountCreateRequest request) throws Exception;

    List<AccountCreateResponse> getAllAccounts();

    AccountCreateResponse updateAccount(String id, @Valid AccountUpdateRequest request);

    AccountCreateResponse getAccountById(String id);

    void deleteAccountById(String id);

    AccountCreateResponse getMyAccount();
}
