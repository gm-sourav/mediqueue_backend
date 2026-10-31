package com.mediqueue.mediqueue_backend.service;

import com.mediqueue.mediqueue_backend.dto.PrescriptionRequest;
import com.mediqueue.mediqueue_backend.dto.PrescriptionResponse;
import com.mediqueue.mediqueue_backend.entity.Appointment;
import com.mediqueue.mediqueue_backend.entity.AppointmentStatus;
import com.mediqueue.mediqueue_backend.entity.Patient;
import com.mediqueue.mediqueue_backend.entity.Prescription;
import com.mediqueue.mediqueue_backend.repository.AppointmentRepository;
import com.mediqueue.mediqueue_backend.repository.PatientRepository;
import com.mediqueue.mediqueue_backend.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public PrescriptionResponse create(String doctorEmail, Long appointmentId, PrescriptionRequest req) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Appointment not found"));

        if (!a.getDoctor().getUser().getEmail().equals(doctorEmail)) {
            throw new AccessDeniedException("Not your appointment");
        }
        if (a.getStatus() != AppointmentStatus.CONFIRMED && a.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Prescription can be written only for confirmed or completed appointments");
        }
        if (prescriptionRepository.existsByAppointmentId(appointmentId)) {
            throw new IllegalStateException("Prescription already exists for this appointment");
        }

        Prescription p = new Prescription();
        p.setAppointment(a);
        p.setDiagnosis(req.getDiagnosis());
        p.setMedicines(req.getMedicines());
        p.setAdvice(req.getAdvice());
        return toResponse(prescriptionRepository.save(p));
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getForAppointment(String patientEmail, Long appointmentId) {
        Prescription p = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Prescription not found"));
        if (!p.getAppointment().getPatient().getUser().getEmail().equals(patientEmail)) {
            throw new AccessDeniedException("Not your prescription");
        }
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> history(String patientEmail) {
        Patient patient = patientRepository.findByUserEmail(patientEmail)
                .orElseThrow(() -> new NoSuchElementException("Patient profile not found"));
        return prescriptionRepository.findByAppointmentPatientIdOrderByCreatedAtDesc(patient.getId())
                .stream().map(this::toResponse).toList();
    }

    private PrescriptionResponse toResponse(Prescription p) {
        Appointment a = p.getAppointment();
        return new PrescriptionResponse(
                p.getId(),
                a.getId(),
                a.getDoctor().getUser().getName(),
                a.getPatient().getUser().getName(),
                a.getSlot().getSlotDate(),
                p.getDiagnosis(),
                p.getMedicines(),
                p.getAdvice());
    }
}