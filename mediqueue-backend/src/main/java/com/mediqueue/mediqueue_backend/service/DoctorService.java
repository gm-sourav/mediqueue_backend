package com.mediqueue.mediqueue_backend.service;

import com.mediqueue.mediqueue_backend.dto.CreateDoctorRequest;
import com.mediqueue.mediqueue_backend.dto.DoctorResponse;
import com.mediqueue.mediqueue_backend.entity.Department;
import com.mediqueue.mediqueue_backend.entity.Doctor;
import com.mediqueue.mediqueue_backend.entity.Role;
import com.mediqueue.mediqueue_backend.entity.User;
import com.mediqueue.mediqueue_backend.repository.DepartmentRepository;
import com.mediqueue.mediqueue_backend.repository.DoctorRepository;
import com.mediqueue.mediqueue_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DoctorResponse create(CreateDoctorRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        Department department = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("Department not found"));

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setRole(Role.DOCTOR);
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(department);
        doctor.setBio(req.getBio());
        doctor.setExperienceYears(req.getExperienceYears());
        doctor.setFee(req.getFee());
        doctor.setPhotoUrl(req.getPhotoUrl());
        doctorRepository.save(doctor);

        return toResponse(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> search(Long departmentId, String name) {
        String nameFilter = (name == null || name.isBlank()) ? null : name.trim();
        return doctorRepository.search(departmentId, nameFilter)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DoctorResponse getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found"));
        return toResponse(doctor);
    }

    private DoctorResponse toResponse(Doctor d) {
        return new DoctorResponse(
                d.getId(),
                d.getUser().getName(),
                d.getUser().getEmail(),
                d.getUser().getPhone(),
                d.getDepartment().getId(),
                d.getDepartment().getName(),
                d.getBio(),
                d.getExperienceYears(),
                d.getFee(),
                d.getPhotoUrl());
    }
}