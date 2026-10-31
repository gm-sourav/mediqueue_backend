package com.mediqueue.mediqueue_backend.repository;

import com.mediqueue.mediqueue_backend.entity.Appointment;
import com.mediqueue.mediqueue_backend.entity.AppointmentStatus;
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

    @Query("SELECT MIN(a.tokenNo) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId AND a.slot.slotDate = :date " +
            "AND a.status = :status AND a.tokenNo > :current")
    Integer findNextToken(@Param("doctorId") Long doctorId, @Param("date") LocalDate date,
                          @Param("status") AppointmentStatus status, @Param("current") int current);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId AND a.slot.slotDate = :date " +
            "AND a.status = :status AND a.tokenNo > :current")
    long countWaiting(@Param("doctorId") Long doctorId, @Param("date") LocalDate date,
                      @Param("status") AppointmentStatus status, @Param("current") int current);
}