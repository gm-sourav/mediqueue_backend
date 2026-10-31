package com.mediqueue.mediqueue_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PrescriptionResponse {
    private Long id;
    private Long appointmentId;
    private String doctorName;
    private String patientName;
    private LocalDate visitDate;
    private String diagnosis;
    private String medicines;
    private String advice;
}