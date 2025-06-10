//package com.study.authenticationservice.config;
//
//import com.study.authenticationservice.constant.PredefinedRole;
//import com.study.authenticationservice.entity.Account;
//import com.study.authenticationservice.entity.Role;
//import com.study.authenticationservice.repository.AccountDAO;
//import com.study.authenticationservice.repository.RoleDAO;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.experimental.NonFinal;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.HashSet;
//
//@Configuration
//@Slf4j
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class AppInitConfig {
//    @NonFinal
//    static final String ADMIN_USER_NAME = "admin";
//
//    @NonFinal
//    static final String ADMIN_PASSWORD = "admin";
//    @Bean
//    ApplicationRunner applicationRunner(AccountDAO  accountDAO, RoleDAO roleDAO, PasswordEncoder passwordEncoder) {
//        log.info("Initializing application.....");
//        return args -> {
//            if (accountDAO.findByUsername(ADMIN_USER_NAME).isEmpty()) {
//               roleDAO.save(Role.builder()
//                        .name(PredefinedRole.USER_ROLE)
//                        .description("User role")
//                        .build());
//
//                Role adminRole = roleDAO.save(Role.builder()
//                        .name(PredefinedRole.ADMIN_ROLE)
//                        .description("Admin role")
//                        .build());
//
//                var roles = new HashSet<Role>();
//                roles.add(adminRole);
//
//                Account account= Account.builder()
//                        .username(ADMIN_USER_NAME)
//                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
//                        .roles(roles)
//                        .build();
//
//                accountDAO.save(account);
//                System.out.println("da luu");
//                log.warn("admin user has been created with default password: admin, please change it");
//            }
//            log.info("Application initialization completed .....");
//        };
//    }
//
//}
