package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.PrescriptionRequest;
import com.mediqueue.mediqueue_backend.dto.PrescriptionResponse;
import com.mediqueue.mediqueue_backend.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/doctor/appointments/{id}/prescription")
    public ResponseEntity<PrescriptionResponse> create(@PathVariable Long id,
                                                       @Valid @RequestBody PrescriptionRequest req,
                                                       Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.create(auth.getName(), id, req));
    }

    @GetMapping("/patient/appointments/{id}/prescription")
    public PrescriptionResponse forAppointment(@PathVariable Long id, Authentication auth) {
        return prescriptionService.getForAppointment(auth.getName(), id);
    }

    @GetMapping("/patient/prescriptions")
    public List<PrescriptionResponse> history(Authentication auth) {
        return prescriptionService.history(auth.getName());
    }
}