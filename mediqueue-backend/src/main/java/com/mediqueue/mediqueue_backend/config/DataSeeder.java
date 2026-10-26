package com.mediqueue.mediqueue_backend.config;

import com.mediqueue.mediqueue_backend.entity.Role;
import com.mediqueue.mediqueue_backend.entity.User;
import com.mediqueue.mediqueue_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@mediqueue.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@mediqueue.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}