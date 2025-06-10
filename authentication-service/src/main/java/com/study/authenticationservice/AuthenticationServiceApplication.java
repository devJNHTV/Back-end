package com.study.authenticationservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableDubbo
public class AuthenticationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}

}
