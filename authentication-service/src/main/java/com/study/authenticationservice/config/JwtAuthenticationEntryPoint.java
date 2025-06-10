package com.study.authenticationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.authenticationservice.Utils.MessageUtils;
import com.study.authenticationservice.dto.response.ApiResponseWrapper;
import com.study.authenticationservice.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    private MessageUtils messageUtils;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponseWrapper<?> apiResponse = ApiResponseWrapper.builder()
                .status(errorCode.getCode())
                .message(messageUtils.getMessage(errorCode.getMessage()))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}