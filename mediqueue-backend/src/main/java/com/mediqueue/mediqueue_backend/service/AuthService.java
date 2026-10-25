package com.mediqueue.mediqueue_backend.service;

import com.mediqueue.mediqueue_backend.dto.RegisterRequest;
import com.mediqueue.mediqueue_backend.dto.UserResponse;
import com.mediqueue.mediqueue_backend.entity.Patient;
import com.mediqueue.mediqueue_backend.entity.Role;
import com.mediqueue.mediqueue_backend.entity.User;
import com.mediqueue.mediqueue_backend.repository.PatientRepository;
import com.mediqueue.mediqueue_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerPatient(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setRole(Role.PATIENT);
        userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(user);
        patientRepository.save(patient);

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}