package com.example.votcha.config;

import com.example.votcha.users.domain.model.Role;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${initial.admin.email}")
    private String adminEmail;

    @Value("${initial.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        boolean adminExists = usersRepo.existsByRole(Role.SUPER_ADMIN);
        if(!adminExists){
            Users superAdmin = Users.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.SUPER_ADMIN)
                    .firstName("Super")
                    .lastName("Admin")
                    .createdAt(Instant.now())
                    .isVerified(true)
                    .build();
            usersRepo.save(superAdmin);
        }
    }
}
