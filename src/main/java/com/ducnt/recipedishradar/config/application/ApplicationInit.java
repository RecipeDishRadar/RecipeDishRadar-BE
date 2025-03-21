package com.ducnt.recipedishradar.config.application;

import com.ducnt.recipedishradar.enums.AccountRole;
import com.ducnt.recipedishradar.enums.AccountStatus;
import com.ducnt.recipedishradar.models.Account;
import com.ducnt.recipedishradar.repositories.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInit {
    @NonFinal
    @Value("${server.account.admin}")
            String SERVER_ACCOUNT_ADMIN;

    PasswordEncoder passwordEncoder;
    AccountRepository accountRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            if(accountRepository.findByEmail(SERVER_ACCOUNT_ADMIN).isEmpty()) {
                Account admin = Account
                        .builder()
                        .email(SERVER_ACCOUNT_ADMIN)
                        .password(passwordEncoder.encode("admin"))
                        .status(AccountStatus.ACTIVE)
                        .dob(LocalDate.now())
                        .role(AccountRole.ADMIN)
                        .build();
                accountRepository.save(admin);
                log.warn("Admin account created with email: {} and password: admin, please change it", SERVER_ACCOUNT_ADMIN);
            }
        };
    }
}
