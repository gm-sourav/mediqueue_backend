package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.AppointmentResponse;
import com.mediqueue.mediqueue_backend.dto.BookAppointmentRequest;
import com.mediqueue.mediqueue_backend.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/patient/appointments")
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody BookAppointmentRequest req,
                                                    Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.book(auth.getName(), req));
    }

    @GetMapping("/patient/appointments")
    public List<AppointmentResponse> mine(Authentication auth) {
        return appointmentService.myAppointments(auth.getName());
    }

    @PutMapping("/patient/appointments/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable Long id, Authentication auth) {
        return appointmentService.cancel(auth.getName(), id);
    }

    @GetMapping("/doctor/appointments")
    public List<AppointmentResponse> doctorList(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication auth) {
        return appointmentService.doctorAppointments(auth.getName(), date);
    }

    @PutMapping("/doctor/appointments/{id}/confirm")
    public AppointmentResponse confirm(@PathVariable Long id, Authentication auth) {
        return appointmentService.confirm(auth.getName(), id);
    }

    @PutMapping("/doctor/appointments/{id}/reject")
    public AppointmentResponse reject(@PathVariable Long id, Authentication auth) {
        return appointmentService.reject(auth.getName(), id);
    }

    @PutMapping("/doctor/appointments/{id}/complete")
    public AppointmentResponse complete(@PathVariable Long id, Authentication auth) {
        return appointmentService.complete(auth.getName(), id);
    }
}