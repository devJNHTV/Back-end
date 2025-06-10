package com.study.profile.service;

import java.util.List;

import jakarta.ws.rs.core.Response;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.study.profile.dto.request.RegistrationRequest;
import com.study.profile.dto.response.ProfileResponse;
import com.study.profile.external.IdentityClientWebclient;
import com.study.profile.mapper.ProfileMapper;
import com.study.profile.repository.IdentityClient;
import com.study.profile.repository.ProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    IdentityClient identityClient;
    IdentityClientWebclient identityClientWebclient;

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;

    public List<ProfileResponse> getAllProfiles() {
        var profiles = profileRepository.findAll();
        return profiles.stream().map(profileMapper::toProfileResponse).toList();
    }

    public ProfileResponse register(RegistrationRequest request) {

        //                var token = identityClientWebclient.getTokenClient(TokenExchangeParam.builder()
        //                        .grant_type("client_credentials")
        //                        .client_id(clientId)
        //                        .client_secret(clientSecret)
        //                        .scope("openid")
        //                .build());
        //
        //
        //        log.info("TokenInfo {}", token);
        //
        //        try {
        //            var creationResponse = identityClientWebclient.createUserKeyCloak(
        //                    UserCreationParam.builder()
        //                            .username(request.getUsername())
        //                            .firstName(request.getFirstName())
        //                            .lastName(request.getLastName())
        //                            .email(request.getEmail())
        //                            .enabled(true)
        //                            .emailVerified(false)
        //                            .credentials(List.of(Credential.builder()
        //                                    .type("password")
        //                                    .temporary(false)
        //                                    .value(request.getPassword())
        //                                    .build()))
        //                            .build(),token.getAccessToken());
        //
        //            log.info(String.valueOf(creationResponse.getStatusCode().value()));
        //            //get User id in keycloak by Header
        //            String userId = extractUserId(creationResponse);
        //            log.info("UserId {}", userId);
        //            var profile = profileMapper.toProfile(request);
        ////            profile.setUserId(userId);
        //
        //            profile = profileRepository.save(profile);
        //            return profileMapper.toProfileResponse(profile);
        ////        }
        //        catch (WebClientResponseException ex)
        //        {
        //
        //            throw  ex;
        //        }
        // Create KLey Cloak admin
        log.info("create amdin");
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8180")
                .realm("studyeasy")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
        // Create user info
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        // Goi api create user in keycloak
        log.info("create amdin");
        Response response = keycloak.realm("studyeasy").users().create(user);
        int status = response.getStatus();
        String userId = CreatedResponseUtil.getCreatedId(response);
        log.info("UserId {}", userId);
        var profile = profileMapper.toProfile(request);
        profile.setUserId(userId);

        profile = profileRepository.save(profile);
        return profileMapper.toProfileResponse(profile);
        //        if (status == 201) {
        //            // Thành công, trả về userId
        //            return CreatedResponseUtil.getCreatedId(response);
        //        } else if (status == 409) {
        //            // Lỗi trùng email hoặc username
        //            throw new AppException(ErrorCode.CONFLICT_KEYCLOAK);
        //        } else if (status == 400) {
        //            // Lỗi đầu vào không hợp lệ
        //            String error = response.readEntity(String.class);
        //            throw new KeycloakCreateUserException("Dữ liệu đầu vào không hợp lệ: " + error);
        //        } else if (status == 500) {
        //            // Lỗi phía server Keycloak
        //            throw new KeycloakCreateUserException("Lỗi phía server Keycloak.");
        //        } else {
        //            // Lỗi không xác định
        //            throw new KeycloakCreateUserException("Lỗi không xác định, status = " + status);
        //        }

    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().getFirst("Location");
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }
}
