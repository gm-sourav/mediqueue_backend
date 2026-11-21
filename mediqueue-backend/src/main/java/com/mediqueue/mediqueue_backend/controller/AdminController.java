package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.*;
import com.mediqueue.mediqueue_backend.service.DepartmentService;
import com.mediqueue.mediqueue_backend.service.AdminStatsService;
import com.mediqueue.mediqueue_backend.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final AdminStatsService statsService;

    @GetMapping("/stats")
    public AdminStatsResponse stats() {
        return statsService.stats();
    }

    @PostMapping("/departments")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(req));
    }

    @PostMapping("/doctors")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody CreateDoctorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(req));
    }

    @PutMapping("/doctors/{id}")
    public DoctorResponse updateDoctor(@PathVariable Long id, @Valid @RequestBody UpdateDoctorRequest req) {
        return doctorService.update(id, req);
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/doctors/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DoctorResponse uploadDoctorPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return doctorService.updatePhoto(id, file);
    }
}