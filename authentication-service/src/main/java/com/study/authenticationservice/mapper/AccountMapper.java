package com.study.authenticationservice.mapper;

import com.study.authenticationservice.dto.request.AccountCreateRequest;
import com.study.authenticationservice.dto.request.AccountUpdateRequest;
import com.study.authenticationservice.dto.response.AccountCreateResponse;
import com.study.authenticationservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    Account toAccount(AccountCreateRequest request);
    AccountCreateResponse toAccountCreateResponse(Account account);
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateAccount(@MappingTarget Account account, AccountUpdateRequest request);
}