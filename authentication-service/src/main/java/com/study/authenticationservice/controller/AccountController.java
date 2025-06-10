package com.study.authenticationservice.controller;

import com.study.authenticationservice.Utils.MessageUtils;
import com.study.authenticationservice.dto.request.AccountCreateRequest;
import com.study.authenticationservice.dto.request.AccountUpdateRequest;
import com.study.authenticationservice.dto.response.AccountCreateResponse;
import com.study.authenticationservice.dto.response.ApiResponseWrapper;
import com.study.authenticationservice.entity.Account;
import com.study.authenticationservice.handler.AccountHandler;
import com.study.authenticationservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/accounts")
@Validated
@CrossOrigin(origins = {"${app.cors.allowed-origins}"}) // Từ config
public class AccountController {
        AccountService accountService;
        AccountHandler accountHandler;
        KafkaTemplate<String,String> kafkaTemplate;
        MessageUtils messageUtils;

        @Operation(summary = "Create Account", description = "Create a new user account")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Account creation successful",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "400", description = "Bad request, invalid input",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        ))
        })
        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
        public ResponseEntity<ApiResponseWrapper<AccountCreateResponse>> createAccount(
                @ModelAttribute @Valid AccountCreateRequest request) {
                
                log.info("Creating account for username: {}", request.getUsername());

                AccountCreateResponse accountCreateResponse = accountHandler.handleCreateAccount(request);
                
                ApiResponseWrapper<AccountCreateResponse> response = new ApiResponseWrapper<>(
                        HttpStatus.CREATED.value(),
                        messageUtils.getMessage("account.create.success"),
                        accountCreateResponse
                );

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Get all accounts with pagination", description = "API to fetch all account information with pagination and sorting")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Successfully fetched all accounts",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "403", description = "Forbidden access",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        ))
        })
        @GetMapping
        @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
        @Cacheable(value = "accounts", key = "#page + '_' + #size + '_' + #sortBy + '_' + #sortDir")
        public ResponseEntity<ApiResponseWrapper<Page<AccountCreateResponse>>> getAccounts(
                @Parameter(description = "Page number (0-based)")
                @RequestParam(defaultValue = "0") @Min(0) int page,
                
                @Parameter(description = "Page size")
                @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
                
                @Parameter(description = "Sort field")
                @RequestParam(defaultValue = "createdAt") String sortBy,
                
                @Parameter(description = "Sort direction")
                @RequestParam(defaultValue = "desc") String sortDir,
                
                @Parameter(description = "Search keyword")
                @RequestParam(required = false) String search) {
                
                log.info("Fetching accounts - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                        page, size, sortBy, sortDir);

                Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
                Pageable pageable = PageRequest.of(page, size, sort);
                
                Page<AccountCreateResponse> accountResponses = accountService.getAllAccounts(pageable, search);
                
                var authentication = SecurityContextHolder.getContext().getAuthentication();
                log.debug("User {} accessed accounts list", authentication.getName());

                ApiResponseWrapper<Page<AccountCreateResponse>> response = new ApiResponseWrapper<>(
                        HttpStatus.OK.value(),
                        messageUtils.getMessage("account.get-all.success"),
                        accountResponses
                );

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get Account by ID", description = "Retrieve account details by account ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account details retrieved successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "400", description = "Bad request, invalid account ID",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "404", description = "Account not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        ))
        })
        @GetMapping("/{id}")
        @Cacheable(value = "account", key = "#id")
        public ResponseEntity<ApiResponseWrapper<AccountCreateResponse>> getAccountById(
                @PathVariable String id) {
                
                log.info("Fetching account with ID: {}", id);

                AccountCreateResponse accountResponse = accountService.getAccountById(id);
                
                ApiResponseWrapper<AccountCreateResponse> response = new ApiResponseWrapper<>(
                        HttpStatus.OK.value(),
                        messageUtils.getMessage("account.get.success"),
                        accountResponse
                );

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get Current User Information", description = "Retrieve the information of the currently authenticated user")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Current user information retrieved successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "401", description = "Unauthorized, user is not logged in",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        ))
        })
        @GetMapping("/me")
        @Cacheable(value = "current-user", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
        public ResponseEntity<ApiResponseWrapper<AccountCreateResponse>> getMyInfo() {
                log.info("Fetching current user information");

                AccountCreateResponse accountResponse = accountService.getMyAccount();
                
                ApiResponseWrapper<AccountCreateResponse> response = new ApiResponseWrapper<>(
                        HttpStatus.OK.value(),
                        messageUtils.getMessage("account.getMyinfo.success"),
                        accountResponse
                );

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Update Account", description = "Update an existing user account by ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account updated successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "400", description = "Bad request, invalid input data",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "404", description = "Account not found for the given ID",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        ))
        })
        @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id)")
        @CacheEvict(value = {"account", "accounts", "current-user"}, allEntries = true)
        public ResponseEntity<ApiResponseWrapper<AccountCreateResponse>> updateAccount(
                @PathVariable String id,
                @ModelAttribute @Valid AccountUpdateRequest request) {

                log.info("Updating account with ID: {}", id);

                AccountCreateResponse accountCreateResponse = accountService.updateAccount(id, request);
                
                ApiResponseWrapper<AccountCreateResponse> response = new ApiResponseWrapper<>(
                        HttpStatus.OK.value(),
                        messageUtils.getMessage("account.update.success"),
                        accountCreateResponse
                );

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Delete Account", description = "Delete an existing user account by ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Account not found for the given ID",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        )),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiResponseWrapper.class)
                        ))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @CacheEvict(value = {"account", "accounts", "current-user"}, allEntries = true)
        public ResponseEntity<Void> deleteAccount(@PathVariable String id) {
                log.info("Deleting account with ID: {}", id);

                accountService.deleteAccountById(id);
                
                return ResponseEntity.noContent().build();
        }
}