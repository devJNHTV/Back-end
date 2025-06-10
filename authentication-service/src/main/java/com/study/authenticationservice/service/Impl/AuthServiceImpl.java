//package com.study.authenticationservice.service.Impl;
//
//import com.nimbusds.jose.*;
//import com.nimbusds.jose.crypto.MACSigner;
//import com.nimbusds.jose.crypto.MACVerifier;
//import com.nimbusds.jwt.JWTClaimsSet;
//import com.nimbusds.jwt.SignedJWT;
//import com.study.authenticationservice.Utils.MessageUtils;
//import com.study.authenticationservice.dto.request.AuthenticationRequest;
//import com.study.authenticationservice.dto.request.IntrospectRequest;
//import com.study.authenticationservice.dto.request.LogoutRequest;
//import com.study.authenticationservice.dto.response.AccountCreateResponse;
//import com.study.authenticationservice.dto.response.AuthenticationResponse;
//import com.study.authenticationservice.dto.response.IntrospectResponse;
//import com.study.authenticationservice.entity.Account;
//import com.study.authenticationservice.entity.InvalidatedToken;
//import com.study.authenticationservice.exception.AppException;
//import com.study.authenticationservice.exception.ErrorCode;
//import com.study.authenticationservice.repository.AccountDAO;
//import com.study.authenticationservice.repository.InvalidatedTokenDAO;
//
//
//
//import com.study.comonlibrary.Service.CommonService;
//import com.study.comonlibrary.dto.NotificationEvent;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.experimental.NonFinal;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.text.ParseException;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class AuthServiceImpl implements AuthenticationService {
////    @DubboReference
////    CommonService commonService;
//    InvalidatedTokenDAO invalidatedTokenDAO;
//    AccountDAO accountDAO;
//    MessageUtils messageUtils;
//    WebClient webClient;
////    @NonFinal
////    @Value("${jwt.signerKey}")
////    protected   String SIGNER_KEY;
//
//
//    @Override
//    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
////        log.info("Authentication attempt for username: {}", request.getUsername());
////
////        Account account = accountDAO.findByUsername(request.getUsername())
////                .orElseThrow(() -> {
////                    log.error("Authentication failed: User {} does not exist", request.getUsername());
////                    return new AppException(ErrorCode.USER_NOTEXISTED);
////                });
////
////        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
////        boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());
////
////        if (!authenticated) {
////            log.error("Authentication failed: Invalid password for username: {}", request.getUsername());
////            throw new AppException(ErrorCode.UNAUTHENTICATED);
////        }
////
////        //String token = generateToken(account);
////        log.info("Authentication successful for username: {}. Token generated.", request.getUsername());
////
////        ///  Message from notifycation-service by Dubbo
////        log.info(commonService.senMessage(request.getUsername()));
////
////        return AuthenticationResponse.builder()
////                .token(token)
////                .Authenticated(authenticated)
////                .build();
//        return AuthenticationResponse.builder()
//                .token("")
//                .Authenticated(false)
//                .build();
//    }
//
////    public IntrospectResponse introspect(IntrospectRequest request)
////            throws JOSEException, ParseException {
////        var token = request.getToken();
////        boolean isValid = true;
////
////        try {
////            verifyToken(token);
////        } catch (AppException e) {
////            isValid = false;
////        }
////
////        return IntrospectResponse.builder()
////                .valid(isValid)
////                .build();
////    }
////    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
////        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
////
////        SignedJWT signedJWT = SignedJWT.parse(token);
////
////        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
////
////        var verified = signedJWT.verify(verifier);
////
////        if (!(verified && expiryTime.after(new Date())))
////            throw new AppException(ErrorCode.UNAUTHENTICATED);
////
////        if (invalidatedTokenDAO.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
////            throw new AppException(ErrorCode.UNAUTHENTICATED);
////
////        return signedJWT;
////    }
////    @Override
////    public void logout(LogoutRequest request) throws JOSEException, ParseException {
////        var signToken = verifyToken(request.getToken());
////
////        String jit = signToken.getJWTClaimsSet().getJWTID();
////        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
////
////        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
////                .id(jit)
////                .expiryTime(expiryTime)
////                .build();
////
////          invalidatedTokenDAO.save(invalidatedToken);
////    }
//
//    @Override
//    public String sendNotificationToUser(String to, String message) {
//        NotificationEvent notificationEvent = NotificationEvent.builder()
//                .channel("Email")
//                .recipient(to)
//                .subject("Welcome New Member")
//                .body("Welcome " + to + " to our website,"+message)
//                .build();
//        return webClient.post()
//                .uri("http://localhost:8082/notification/send") // Đường dẫn đến API trong Notification Service
//                .bodyValue(notificationEvent)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block(); // Sử dụng block() để chờ kết quả đồng bộ, nếu cần
//    }
//
////    private String generateToken(Account account) {
////        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
////
////        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
////                .subject(account.getUsername())
////                .issuer("https://study.com")
////                .issueTime(new Date())
////                .expirationTime(new Date(
////                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
////                ))
////                .jwtID(UUID.randomUUID().toString())
////                .claim("scope", buildScope(account))
////                .build();
////
////        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
////
////        JWSObject jwsObject = new JWSObject(header, payload);
////
////        try {
////            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
////            return jwsObject.serialize();
////        } catch (JOSEException e) {
////            log.error("Cannot create token", e);
////            throw new RuntimeException(e);
////        }
////    }
////    private String buildScope(Account account) {
////        StringJoiner stringJoiner = new StringJoiner(" ");
////
////        if (!CollectionUtils.isEmpty(account.getRoles()))
////            account.getRoles().forEach(role -> {
////                stringJoiner.add(role.getName());
////                if (!CollectionUtils.isEmpty(role.getPermissions()))
////                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
////            });
////
////        return stringJoiner.toString();
////    }
////    public AuthenticationResponse fallbackMethod(AuthenticationRequest request, Throwable t) {
////
////        log.warn("️CircuitBreaker fallback triggered for user: {}. Reason: {}", request.getUsername(), t.getMessage());
////        throw new AppException(ErrorCode.UNAUTHENTICATED);
////    }
//}
