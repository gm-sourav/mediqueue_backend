package com.mediqueue.mediqueue_backend.service;

import com.mediqueue.mediqueue_backend.dto.DepartmentRequest;
import com.mediqueue.mediqueue_backend.dto.DepartmentResponse;
import com.mediqueue.mediqueue_backend.entity.Department;
import com.mediqueue.mediqueue_backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentResponse create(DepartmentRequest req) {
        if (departmentRepository.existsByName(req.getName())) {
            throw new IllegalArgumentException("Department already exists");
        }
        Department d = new Department();
        d.setName(req.getName());
        d.setIcon(req.getIcon());
        departmentRepository.save(d);
        return toResponse(d);
    }

    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    private DepartmentResponse toResponse(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getIcon());
    }
}