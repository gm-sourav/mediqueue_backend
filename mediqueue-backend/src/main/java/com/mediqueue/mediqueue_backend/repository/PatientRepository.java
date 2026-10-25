package com.mediqueue.mediqueue_backend.repository;

import com.mediqueue.mediqueue_backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}