package com.library.userservice.config;

import com.library.userservice.model.Role;
import com.library.userservice.model.User;
import com.library.userservice.repository.RoleRepository;
import com.library.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role adminRole = Role.builder().role("ADMIN").build();
                Role userRole = Role.builder().role("USER").build();

                roleRepository.saveAll(Arrays.asList(adminRole, userRole));
                log.info("Roles iniciales cargados en la base de datos.");
            }

            if (userRepository.count() == 0) {
                Role adminRole = roleRepository.findByRole("ADMIN");
                Role userRole = roleRepository.findByRole("USER");

                User admin = User.builder()
                        .userName("admin")
                        .name("Admin User")
                        .email("admin@library.com")
                        .password(passwordEncoder.encode("admin"))
                        .role(adminRole)
                        .build();

                User user1 = User.builder()
                        .userName("user1")
                        .name("Normal User")
                        .email("user1@library.com")
                        .password(passwordEncoder.encode("user1"))
                        .role(userRole)
                        .build();

                userRepository.saveAll(Arrays.asList(admin, user1));
                log.info("Usuarios iniciales cargados en la base de datos.");
            }
        };
    }
}
