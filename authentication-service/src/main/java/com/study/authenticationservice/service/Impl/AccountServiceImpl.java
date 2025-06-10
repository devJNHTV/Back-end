package com.study.authenticationservice.service.Impl;

import com.study.authenticationservice.Validator.ValidateImage;
import com.study.authenticationservice.config.TemporalConfig;
import com.study.authenticationservice.dto.request.AccountCreateRequest;
import com.study.authenticationservice.dto.request.AccountUpdateRequest;
import com.study.authenticationservice.dto.response.AccountCreateResponse;
import com.study.authenticationservice.entity.Account;
import com.study.authenticationservice.exception.AppException;
import com.study.authenticationservice.exception.ErrorCode;
import com.study.authenticationservice.mapper.AccountMapper;
import com.study.authenticationservice.repository.AccountDAO;
import com.study.authenticationservice.service.AccountService;
import com.study.authenticationservice.temporal.workflow.GetUserByIDWorkFlow;
import com.study.comonlibrary.Service.CommonService;
import com.study.comonlibrary.dto.NotificationEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountServiceImpl implements AccountService {

    PasswordEncoder passwordEncoder;
    AccountDAO accountDAO;
    AccountMapper accountMapper;
    KafkaTemplate<String,Object> kafkaTemplate;
    ValidateImage validateImage;
    WorkflowClient workflowClient;
    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;

    @Value("${idp.url}")
    @NonFinal
    String url;

    @Value("${idp.realm}")
    @NonFinal
    String realm;

    @DubboReference
    CommonService commonService;

    @Override
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethodCreate")
    public AccountCreateResponse createAccount(AccountCreateRequest request)  {
        log.info("Starting account creation for username: {}", request.getUsername());

        Account account = accountMapper.toAccount(request);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        log.info("Starting create userid in KeyCloak: {}", request.getUsername());

        String userID = getUserIDFromKeyCloak(request);
        account.setUserId(userID);

        String avatarURL = validateImage.ValidateUploadFile(request.getImage());
        log.info("Avatar uploaded successfully. URL: {}", avatarURL);
        account.setImage(avatarURL);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("Email")
                .recipient(request.getEmail())
                .subject("Welcome New Member")
                .body("Welcome " + request.getUsername() + " to our website, create some amazing things together")
                .build();

        kafkaTemplate.send("send-email-welcome", notificationEvent);
        log.info("Sent welcome email to: {}", request.getEmail());
        Account savedAccount = accountDAO.save(account);
        log.info("Account saved successfully with ID: {}", savedAccount.getId());

        return accountMapper.toAccountCreateResponse(savedAccount);
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value="users")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    public List<AccountCreateResponse> getAllAccounts() {
        log.info("Getting info from database");
        List<Account> accounts = accountDAO.findAll();
        log.info("Create cache with key 'getALl' in redis");
        return accounts.stream().map(accountMapper:: toAccountCreateResponse).collect(Collectors.toList());
    }
    @Override
    @CachePut(value = "users", key = "#id")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethodUpdate")
    public AccountCreateResponse updateAccount(String id, AccountUpdateRequest request) {
        log.info("Updating account with ID: {}", id);
        Account account = accountDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", id);
                    return new AppException(ErrorCode.USER_NOTEXISTED);
                });
        String avatarURL = validateImage.ValidateUploadFile(request.getImage());
        log.info("Avatar uploaded successfully. URL: {}", avatarURL);

        accountMapper.updateAccount(account, request);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setImage(avatarURL);

        updateUserInKeycloak(account.getUserId(), request);
        Account updatedAccount = accountDAO.save(account);
        log.info("Account updated successfully for ID: {}", updatedAccount.getId());
        return accountMapper.toAccountCreateResponse(updatedAccount);
    }

    @Override
    public AccountCreateResponse getAccountById(String id) {
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TemporalConfig.TASK_QUEUE_GET) // Đảm bảo dùng đúng queue
                .build();
        // Tạo stub của workflow
        GetUserByIDWorkFlow workflow = workflowClient.newWorkflowStub(GetUserByIDWorkFlow.class, options);

        return workflow.getUserByID(id);
    }
    @Override
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void deleteAccountById(String id) {
        log.info("Deleting account with ID: {}", id);
        Account account = accountDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", id);
                    return new AppException(ErrorCode.USER_NOTEXISTED);
                });
        deleteUserInKeyCloak(account);
        accountDAO.deleteById(id);
        log.info( commonService.senMessage(account.getUsername()));
        log.info("Account deleted successfully for ID: {}", id);
    }
    @Override
    public AccountCreateResponse getMyAccount() {
        // Lấy userId từ context của Keycloak
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching my account info for userId: {}", userId);
        // Dùng userId để tìm tài khoản
        Account account = (Account) accountDAO.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Account not found with userId: {}", userId);
                    return new AppException(ErrorCode.USER_NOTEXISTED);
                });
        log.info("Fetched my account info successfully for userId: {}", userId);
        return accountMapper.toAccountCreateResponse(account);
    }

    public List<AccountCreateResponse> fallbackMethod(Exception ex) {
        // Ghi lại lỗi vào log để theo dõi
        log.error("Error occurred while fetching accounts: ", ex);
        // Trả về danh sách trống hoặc dữ liệu mặc định thay vì gây lỗi cho client
        return Collections.emptyList();
    }
    public AccountCreateResponse fallbackMethodCreate(AccountCreateRequest request, Throwable throwable) {
        // Log chi tiết lỗi gốc để dễ debug
        log.error("[createAccount][fallback] Error while creating account for username={} → {}",
                request.getUsername(), throwable.getMessage(), throwable);
        throw new AppException(ErrorCode.CONFLICT_KEYCLOAK);
    }
    public AccountCreateResponse fallbackMethodUpdate(String id,AccountUpdateRequest request,
                                                      Throwable throwable) {
        log.error("[updateAccount][fallback] Cannot update account id={}, cause: {}",
                id, throwable.getMessage(), throwable);
        throw new AppException(ErrorCode.USER_NOTEXISTED);
    }
    private Keycloak getKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(url)
                 .realm(realm)
               .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
    public String getUserIDFromKeyCloak(AccountCreateRequest request) {
           try {
               Keycloak keycloak = getKeycloakClient();
               // Tạo user thông tin
               UserRepresentation user = new UserRepresentation();
               user.setUsername(request.getUsername());
               user.setEmail(request.getEmail());
               user.setFirstName(request.getFirstName());
               user.setLastName(request.getLastName());
               user.setEnabled(true);

               // Gửi yêu cầu tạo user
               Response response = keycloak.realm(realm).users().create(user);
               int status = response.getStatus();
               log.info(String.valueOf(status));
               // Thành công: lấy userId và thiết lập mật khẩu
               String userId = CreatedResponseUtil.getCreatedId(response);

               CredentialRepresentation passwordCred = new CredentialRepresentation();
               passwordCred.setTemporary(false);
               passwordCred.setType(CredentialRepresentation.PASSWORD);
               passwordCred.setValue(request.getPassword());

               keycloak.realm(realm).users().get(userId).resetPassword(passwordCred);
               return userId;
           } catch (Exception e) {
               throw new AppException(ErrorCode.UNCATERROR_ERROR);
           }
    }
    public void deleteUserInKeyCloak(Account account) {
        try (Keycloak keycloak = getKeycloakClient  ()) {
            Response response = keycloak.realm(realm)
                    .users()
                    .delete(account.getUserId());
            int status = response.getStatus();
            String userId= account.getUserId();
            if (status == 204) {
                log.info("[deleteAccountById] Keycloak user deleted successfully, userId={}", userId);
            } else if (status == 404) {
                log.warn("[deleteAccountById] Keycloak user not found (already deleted?), userId={}", userId);
            } else {
                String errorDetail = response.readEntity(String.class);
                log.error("[deleteAccountById] Failed to delete Keycloak user, status={}, detail={}", status, errorDetail);
                throw new AppException(ErrorCode.UNCATERROR_ERROR);
            }
        } catch (AppException ae) {
            throw ae; // bubble up known AppException
        } catch (Exception ex) {
            log.error("[deleteAccountById] Exception when deleting Keycloak user, userId ");
            throw new AppException(ErrorCode.UNCATERROR_ERROR);
        }
    }
    public void updateUserInKeycloak(String userId, AccountUpdateRequest request) {
        try ( Keycloak keycloak = getKeycloakClient()) {

            // Lấy đối tượng user
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            UserRepresentation user = userResource.toRepresentation();

            // Cập nhật thông tin (tùy theo bạn cho phép user cập nhật gì)
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            // Gửi update lên Keycloak
            userResource.update(user);

            CredentialRepresentation newPassword = new CredentialRepresentation();
            newPassword.setType(CredentialRepresentation.PASSWORD);
            newPassword.setValue(request.getPassword());
            newPassword.setTemporary(false);
            userResource.resetPassword(newPassword);
            log.info("User info updated on Keycloak for userId: {}", userId);
        } catch (Exception e) {
            log.error("Error updating user on Keycloak for userId: {}", userId, e);
            throw new RuntimeException("Failed to update user in Keycloak", e);
        }
    }

}
