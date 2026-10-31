package com.mediqueue.mediqueue_backend.service;

import com.mediqueue.mediqueue_backend.dto.CreateSlotRequest;
import com.mediqueue.mediqueue_backend.dto.SlotResponse;
import com.mediqueue.mediqueue_backend.entity.Doctor;
import com.mediqueue.mediqueue_backend.entity.Slot;
import com.mediqueue.mediqueue_backend.entity.SlotStatus;
import com.mediqueue.mediqueue_backend.repository.DoctorRepository;
import com.mediqueue.mediqueue_backend.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository slotRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public List<SlotResponse> createSlots(String doctorEmail, CreateSlotRequest req) {
        Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                .orElseThrow(() -> new NoSuchElementException("Doctor profile not found"));

        if (req.getSlotDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Slot date cannot be in the past");
        }
        if (!req.getEndTime().isAfter(req.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        int step = req.getSlotDurationMinutes();
        List<Slot> created = new ArrayList<>();
        LocalTime start = req.getStartTime();

        while (!start.plusMinutes(step).isAfter(req.getEndTime())) {
            LocalTime end = start.plusMinutes(step);
            boolean exists = slotRepository.existsByDoctorIdAndSlotDateAndStartTime(
                    doctor.getId(), req.getSlotDate(), start);
            if (!exists) {
                Slot s = new Slot();
                s.setDoctor(doctor);
                s.setSlotDate(req.getSlotDate());
                s.setStartTime(start);
                s.setEndTime(end);
                s.setStatus(SlotStatus.AVAILABLE);
                created.add(slotRepository.save(s));
            }
            if (end.isBefore(start)) break; // midnight wrap guard
            start = end;
        }

        if (created.isEmpty()) {
            throw new IllegalStateException("No new slots created (they may already exist)");
        }
        return created.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> availableSlots(Long doctorId, LocalDate date) {
        return slotRepository
                .findByDoctorIdAndSlotDateAndStatusOrderByStartTime(doctorId, date, SlotStatus.AVAILABLE)
                .stream().map(this::toResponse).toList();
    }

    private SlotResponse toResponse(Slot s) {
        return new SlotResponse(s.getId(), s.getDoctor().getId(),
                s.getSlotDate(), s.getStartTime(), s.getEndTime(), s.getStatus());
    }
}