package com.mediqueue.mediqueue_backend.service;

import com.mediqueue.mediqueue_backend.dto.AppointmentResponse;
import com.mediqueue.mediqueue_backend.dto.BookAppointmentRequest;
import com.mediqueue.mediqueue_backend.entity.*;
import com.mediqueue.mediqueue_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SlotRepository slotRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public AppointmentResponse book(String patientEmail, BookAppointmentRequest req) {
        Patient patient = patientRepository.findByUserEmail(patientEmail)
                .orElseThrow(() -> new NoSuchElementException("Patient profile not found"));
        Slot slot = slotRepository.findById(req.getSlotId())
                .orElseThrow(() -> new NoSuchElementException("Slot not found"));

        LocalDateTime slotStart = LocalDateTime.of(slot.getSlotDate(), slot.getStartTime());
        if (slotStart.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This slot time has already passed");
        }

        int updated = slotRepository.updateStatusIfCurrent(
                slot.getId(), SlotStatus.AVAILABLE, SlotStatus.BOOKED);
        if (updated == 0) {
            throw new IllegalStateException("Slot already booked");
        }

        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(slot.getDoctor());
        a.setSlot(slot);
        a.setReason(req.getReason());
        a.setStatus(AppointmentStatus.PENDING);
        return toResponse(appointmentRepository.save(a));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> myAppointments(String patientEmail) {
        Patient patient = patientRepository.findByUserEmail(patientEmail)
                .orElseThrow(() -> new NoSuchElementException("Patient profile not found"));
        return appointmentRepository.findByPatientIdOrderByCreatedAtDesc(patient.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AppointmentResponse cancel(String patientEmail, Long id) {
        Appointment a = find(id);
        if (!a.getPatient().getUser().getEmail().equals(patientEmail)) {
            throw new AccessDeniedException("Not your appointment");
        }
        if (a.getStatus() != AppointmentStatus.PENDING && a.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending or confirmed appointments can be cancelled");
        }
        a.setStatus(AppointmentStatus.CANCELLED);
        slotRepository.updateStatusIfCurrent(a.getSlot().getId(), SlotStatus.BOOKED, SlotStatus.AVAILABLE);
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> doctorAppointments(String doctorEmail, LocalDate date) {
        Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                .orElseThrow(() -> new NoSuchElementException("Doctor profile not found"));
        return appointmentRepository.findByDoctorIdAndSlotSlotDateOrderBySlotStartTime(doctor.getId(), date)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AppointmentResponse confirm(String doctorEmail, Long id) {
        Appointment a = findOwnedByDoctor(doctorEmail, id);
        if (a.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Only pending appointments can be confirmed");
        }
        int next = appointmentRepository.findMaxToken(a.getDoctor().getId(), a.getSlot().getSlotDate()) + 1;
        a.setTokenNo(next);
        a.setStatus(AppointmentStatus.CONFIRMED);
        return toResponse(a);
    }

    @Transactional
    public AppointmentResponse reject(String doctorEmail, Long id) {
        Appointment a = findOwnedByDoctor(doctorEmail, id);
        if (a.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Only pending appointments can be rejected");
        }
        a.setStatus(AppointmentStatus.REJECTED);
        slotRepository.updateStatusIfCurrent(a.getSlot().getId(), SlotStatus.BOOKED, SlotStatus.AVAILABLE);
        return toResponse(a);
    }

    @Transactional
    public AppointmentResponse complete(String doctorEmail, Long id) {
        Appointment a = findOwnedByDoctor(doctorEmail, id);
        if (a.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed appointments can be completed");
        }
        a.setStatus(AppointmentStatus.COMPLETED);
        return toResponse(a);
    }

    private Appointment find(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Appointment not found"));
    }

    private Appointment findOwnedByDoctor(String doctorEmail, Long id) {
        Appointment a = find(id);
        if (!a.getDoctor().getUser().getEmail().equals(doctorEmail)) {
            throw new AccessDeniedException("Not your appointment");
        }
        return a;
    }

    private AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getUser().getName(),
                a.getDoctor().getId(),
                a.getDoctor().getUser().getName(),
                a.getDoctor().getDepartment().getName(),
                a.getSlot().getSlotDate(),
                a.getSlot().getStartTime(),
                a.getSlot().getEndTime(),
                a.getStatus(),
                a.getReason(),
                a.getTokenNo());
    }
}