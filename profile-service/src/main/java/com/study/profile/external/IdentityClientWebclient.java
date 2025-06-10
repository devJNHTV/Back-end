package com.study.profile.external;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.study.profile.dto.identity.TokenExchangeParam;
import com.study.profile.dto.identity.TokenExchangeResponse;
import com.study.profile.dto.identity.UserCreationParam;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdentityClientWebclient {
    final WebClient webClient;

    public TokenExchangeResponse getTokenClient(TokenExchangeParam tokenExchangeParam) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.setAll(Map.of(
                "grant_type", tokenExchangeParam.getGrant_type(),
                "client_id", tokenExchangeParam.getClient_id(),
                "client_secret", tokenExchangeParam.getClient_secret(),
                "scope", tokenExchangeParam.getScope()));

        return webClient
                .post()
                .uri("http://localhost:8180/realms/studyeasy/protocol/openid-connect/token")
                .header("Content-type", "application/x-www-form-urlencoded") // body
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(TokenExchangeResponse.class)
                .block(); // Sử dụng block() để chờ kết quả đồng bộ, nếu cần
    }

    public ResponseEntity<?> createUserKeyCloak(UserCreationParam param, String accessToken) {
        return webClient
                .post()
                .uri("http://localhost:8180/admin/realms/studyeasy/users")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(param)
                .retrieve() // Tự động ném lỗi nếu status không 2xx
                .toEntity(String.class) // Không cần body trả về
                .block();
    }
}
