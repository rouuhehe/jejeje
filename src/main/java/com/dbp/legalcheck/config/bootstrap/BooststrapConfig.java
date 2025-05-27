package com.dbp.legalcheck.config.bootstrap;

import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.domain.user.UserService;
import com.dbp.legalcheck.dto.user.RegisterUserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BooststrapConfig {
    private final UserService userService;
    private Logger logger = LoggerFactory.getLogger(BooststrapConfig.class);

    @Value("${api.admin.email}")
    private String adminEmail;

    @Value("${api.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner defaultAdminBootstrapper() {
        return args -> {
            if (userService.userExistsByRole(UserRole.ADMINISTRADOR)) {
                return;
            }

            logger.info("Creating default administrator");

            RegisterUserDTO defaultAdmin = new RegisterUserDTO();
            defaultAdmin.setEmail(adminEmail);
            defaultAdmin.setPassword(adminPassword);
            User admin = userService.createUser(defaultAdmin);
            userService.updateUserRole(admin, UserRole.ADMINISTRADOR);
            userService.verifyUser(admin);
        };
    }
}
