package com.mediqueue.mediqueue_backend.repository;

import com.mediqueue.mediqueue_backend.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    boolean existsByAppointmentId(Long appointmentId);
    Optional<Prescription> findByAppointmentId(Long appointmentId);
    List<Prescription> findByAppointmentPatientIdOrderByCreatedAtDesc(Long patientId);
}