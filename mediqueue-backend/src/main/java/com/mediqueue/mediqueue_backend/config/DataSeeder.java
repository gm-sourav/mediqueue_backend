package com.mediqueue.mediqueue_backend.config;

import com.mediqueue.mediqueue_backend.entity.Department;
import com.mediqueue.mediqueue_backend.entity.Role;
import com.mediqueue.mediqueue_backend.entity.User;
import com.mediqueue.mediqueue_backend.repository.DepartmentRepository;
import com.mediqueue.mediqueue_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
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

        if (departmentRepository.count() == 0) {
            String[][] depts = {
                    {"Cardiology", "heart"},
                    {"Neurology", "brain"},
                    {"Dental", "tooth"},
                    {"Eye", "eye"},
                    {"Pediatrics", "child"},
                    {"Orthopedics", "bone"}
            };
            for (String[] d : depts) {
                Department dep = new Department();
                dep.setName(d[0]);
                dep.setIcon(d[1]);
                departmentRepository.save(dep);
            }
        }
    }
}