package com.mediqueue.mediqueue_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Long departmentId;
    private String departmentName;
    private String bio;
    private Integer experienceYears;
    private BigDecimal fee;
    private String photoUrl;
}