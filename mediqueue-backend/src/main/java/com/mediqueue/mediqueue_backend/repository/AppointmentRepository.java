package com.mediqueue.mediqueue_backend.repository;

import com.mediqueue.mediqueue_backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<Appointment> findByDoctorIdAndSlotSlotDateOrderBySlotStartTime(Long doctorId, LocalDate date);

    @Query("SELECT COALESCE(MAX(a.tokenNo), 0) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId AND a.slot.slotDate = :date")
    int findMaxToken(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
}