package com.mediqueue.mediqueue_backend.service;

import com.mediqueue.mediqueue_backend.dto.QueueResponse;
import com.mediqueue.mediqueue_backend.entity.AppointmentStatus;
import com.mediqueue.mediqueue_backend.entity.Doctor;
import com.mediqueue.mediqueue_backend.entity.QueueState;
import com.mediqueue.mediqueue_backend.repository.AppointmentRepository;
import com.mediqueue.mediqueue_backend.repository.DoctorRepository;
import com.mediqueue.mediqueue_backend.repository.QueueStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueStateRepository queueStateRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public QueueResponse next(String doctorEmail, LocalDate date) {
        Doctor doctor = findDoctor(doctorEmail);

        QueueState state = queueStateRepository
                .findByDoctorIdAndQueueDate(doctor.getId(), date)
                .orElseGet(() -> {
                    QueueState s = new QueueState();
                    s.setDoctor(doctor);
                    s.setQueueDate(date);
                    s.setCurrentToken(0);
                    return queueStateRepository.save(s);
                });

        Integer next = appointmentRepository.findNextToken(
                doctor.getId(), date, AppointmentStatus.CONFIRMED, state.getCurrentToken());
        if (next == null) {
            throw new IllegalStateException("No more patients in the queue");
        }
        state.setCurrentToken(next);
        queueStateRepository.save(state);
        return build(doctor, date, next);
    }

    @Transactional(readOnly = true)
    public QueueResponse myQueue(String doctorEmail, LocalDate date) {
        return view(findDoctor(doctorEmail), date);
    }

    @Transactional(readOnly = true)
    public QueueResponse viewForDoctor(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found"));
        return view(doctor, date);
    }

    private QueueResponse view(Doctor doctor, LocalDate date) {
        int current = queueStateRepository
                .findByDoctorIdAndQueueDate(doctor.getId(), date)
                .map(QueueState::getCurrentToken)
                .orElse(0);
        return build(doctor, date, current);
    }

    private QueueResponse build(Doctor doctor, LocalDate date, int current) {
        long waiting = appointmentRepository.countWaiting(
                doctor.getId(), date, AppointmentStatus.CONFIRMED, current);
        return new QueueResponse(doctor.getId(), doctor.getUser().getName(), date, current, waiting);
    }

    private Doctor findDoctor(String email) {
        return doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Doctor profile not found"));
    }
}