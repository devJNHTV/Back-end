package com.study.authenticationservice.service;

import com.study.authenticationservice.dto.response.AccountCreateResponse;
import com.study.authenticationservice.entity.Account;
import com.study.authenticationservice.exception.AppException;
import com.study.authenticationservice.exception.ErrorCode;
import com.study.authenticationservice.mapper.AccountMapper;
import com.study.authenticationservice.repository.AccountDAO;
import com.study.authenticationservice.service.Impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountDAO accountDAO;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountCreateResponse accountCreateResponse;

    @BeforeEach
    void setUp() {
        // Khởi tạo dữ liệu giả
        account = new Account();
        account.setUsername("testuser");
        account.setEmail("testuser@example.com");

        accountCreateResponse = new AccountCreateResponse();
        accountCreateResponse.setUsername("testuser");
        accountCreateResponse.setEmail("testuser@example.com");
    }

    @Test
    void getAllAccounts_ShouldReturnAllMappedAccounts() {
        // Tạo một danh sách tài khoản mẫu
        List<Account> accounts = List.of(account, account);
        List<AccountCreateResponse> expectedResponses = List.of(accountCreateResponse, accountCreateResponse);

        // Mock hành vi của accountDAO.findAll() và accountMapper.toAccountCreateResponse()
        when(accountDAO.findAll()).thenReturn(accounts);
        when(accountMapper.toAccountCreateResponse(account)).thenReturn(accountCreateResponse);

        // Gọi phương thức cần kiểm tra
        List<AccountCreateResponse> result = accountService.getAllAccounts();


        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResponses.get(0).getUsername(), result.get(0).getUsername());
        assertEquals(expectedResponses.get(0).getEmail(), result.get(0).getEmail());
        assertEquals(expectedResponses.get(1).getUsername(), result.get(1).getUsername());
        assertEquals(expectedResponses.get(1).getEmail(), result.get(1).getEmail());
        verify(accountDAO, times(1)).findAll();
        verify(accountMapper, times(2)).toAccountCreateResponse(account);
    }

    @Test
    void getAllAccounts_ShouldReturnEmptyList_WhenNoAccountsExist() {
        when(accountDAO.findAll()).thenReturn(List.of());
        List<AccountCreateResponse> result = accountService.getAllAccounts();
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(accountDAO, times(1)).findAll();
        verify(accountMapper, never()).toAccountCreateResponse(any(Account.class));
    }

    @Test
    void getAllAccounts_ShouldThrowUserExistedException_WhenDatabaseErrorOccurs() {
        when(accountDAO.findAll()).thenThrow(new AppException(ErrorCode.USER_EXISTED));
        AppException exception = assertThrows(AppException.class, () -> accountService.getAllAccounts());
        assertEquals(ErrorCode.USER_EXISTED, exception.getErrorCode());
        verify(accountDAO, times(1)).findAll();
        verify(accountMapper, never()).toAccountCreateResponse(any(Account.class));
    }
}