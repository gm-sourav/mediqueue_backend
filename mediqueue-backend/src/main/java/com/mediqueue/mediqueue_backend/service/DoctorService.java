package com.mediqueue.mediqueue_backend.service;

import com.mediqueue.mediqueue_backend.dto.CreateDoctorRequest;
import com.mediqueue.mediqueue_backend.dto.DoctorResponse;
import com.mediqueue.mediqueue_backend.entity.Department;
import com.mediqueue.mediqueue_backend.entity.Doctor;
import com.mediqueue.mediqueue_backend.entity.Role;
import com.mediqueue.mediqueue_backend.entity.User;
import com.mediqueue.mediqueue_backend.dto.UpdateDoctorRequest;
import com.mediqueue.mediqueue_backend.repository.AppointmentRepository;
import com.mediqueue.mediqueue_backend.repository.PasswordResetTokenRepository;
import com.mediqueue.mediqueue_backend.repository.QueueStateRepository;
import com.mediqueue.mediqueue_backend.repository.SlotRepository;
import com.mediqueue.mediqueue_backend.repository.DepartmentRepository;
import com.mediqueue.mediqueue_backend.repository.DoctorRepository;
import com.mediqueue.mediqueue_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhotoStorageService photoStorage;
    private final AppointmentRepository appointmentRepository;
    private final SlotRepository slotRepository;
    private final QueueStateRepository queueStateRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

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
        user.setGender(blankToNull(req.getGender()));
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

    @Transactional
    public DoctorResponse update(Long id, UpdateDoctorRequest req) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found"));
        Department department = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("Department not found"));

        User user = doctor.getUser();
        user.setName(req.getName().trim());
        user.setPhone(blankToNull(req.getPhone()));
        user.setGender(blankToNull(req.getGender()));
        userRepository.save(user);

        doctor.setDepartment(department);
        doctor.setBio(blankToNull(req.getBio()));
        doctor.setExperienceYears(req.getExperienceYears());
        doctor.setFee(req.getFee());
        doctorRepository.save(doctor);

        return toResponse(doctor);
    }

    /**
     * Deletes a doctor with the login account, slots and queue rows.
     * A doctor who already has appointments is NOT deleted (patients' history must stay).
     */
    @Transactional
    public void delete(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found"));
        if (appointmentRepository.existsByDoctorId(id)) {
            throw new IllegalStateException("This doctor has appointments, so cannot be deleted");
        }
        User user = doctor.getUser();
        String photo = user.getPhotoUrl();

        queueStateRepository.deleteByDoctorId(id);
        slotRepository.deleteByDoctorId(id);
        doctorRepository.delete(doctor);
        doctorRepository.flush();
        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);
        userRepository.delete(user);
        photoStorage.delete(photo);
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
                d.getUser().getPhotoUrl() != null ? d.getUser().getPhotoUrl() : d.getPhotoUrl(),
                d.getUser().getGender());
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    /** Admin sets a doctor's photo. Old photo file is deleted. */
    @Transactional
    public DoctorResponse updatePhoto(Long doctorId, MultipartFile file) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found"));
        User user = doctor.getUser();
        String old = user.getPhotoUrl();
        user.setPhotoUrl(photoStorage.save(file));
        userRepository.save(user);
        photoStorage.delete(old);
        return toResponse(doctor);
    }
}