package com.study.authenticationservice.config;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import javax.crypto.spec.SecretKeySpec;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurrityConfig {
    private final String[] PUBLIC_KEY_ENDPOINT = {"/introspect","/token","/actuator/prometheus","/actuator/health","/log-out"
    ,"/accounts/create"};
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // hoặc bạn có thể dùng encoder khác
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(request ->
                request.requestMatchers(PUBLIC_KEY_ENDPOINT ).permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                       .anyRequest().authenticated());

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(
                jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(authenticationEntryPoint));
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new CustomAuthoritiesConverter());

        return jwtAuthenticationConverter;
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}

