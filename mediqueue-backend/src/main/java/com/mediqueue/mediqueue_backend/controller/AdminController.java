package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.*;
import com.mediqueue.mediqueue_backend.service.DepartmentService;
import com.mediqueue.mediqueue_backend.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DepartmentService departmentService;
    private final DoctorService doctorService;

    @PostMapping("/departments")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(req));
    }

    @PostMapping("/doctors")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody CreateDoctorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(req));
    }
}