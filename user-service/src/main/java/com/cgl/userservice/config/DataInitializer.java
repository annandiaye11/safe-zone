package com.cgl.userservice.config;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            if (userRepository.count() != 0) {
                userRepository.deleteAll();
            }

            if (userRepository.count() == 0) {
                log.info("Initializing data");

                User seller = User.builder()
                        .name("Fatima Keita")
                        .email("ftk@user.com")
                        .password(passwordEncoder.encode("seller@123"))
                        .role(Role.SELLER)
                        .avatar("/uploads/0000-0000000-0000-000006-0000.jpg")
                        .build();
                userRepository.save(seller);

                User client = User.builder()
                        .name("Anna Ndiaye")
                        .email("anna@user.com")
                        .password(passwordEncoder.encode("client@123"))
                        .role(Role.CLIENT)
                        .build();
                userRepository.save(client);

                User johndoe = User.builder()
                        .name("John DOE")
                        .email("johndoe@user.com")
                        .password(passwordEncoder.encode("client@1234"))
                        .role(Role.CLIENT)
                        .build();
                userRepository.save(johndoe);
            }
        };
    }

}
