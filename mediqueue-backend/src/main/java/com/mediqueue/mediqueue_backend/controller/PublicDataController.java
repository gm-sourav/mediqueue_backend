package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.DepartmentResponse;
import com.mediqueue.mediqueue_backend.dto.DoctorResponse;
import com.mediqueue.mediqueue_backend.service.DepartmentService;
import com.mediqueue.mediqueue_backend.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicDataController {

    private final DepartmentService departmentService;
    private final DoctorService doctorService;

    @GetMapping("/departments")
    public List<DepartmentResponse> departments() {
        return departmentService.getAll();
    }

    @GetMapping("/doctors")
    public List<DoctorResponse> doctors(@RequestParam(required = false) Long departmentId,
                                        @RequestParam(required = false) String name) {
        return doctorService.search(departmentId, name);
    }

    @GetMapping("/doctors/{id}")
    public DoctorResponse doctor(@PathVariable Long id) {
        return doctorService.getById(id);
    }
}