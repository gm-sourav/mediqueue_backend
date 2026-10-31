package com.mediqueue.mediqueue_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PrescriptionRequest {

    @NotBlank(message = "Diagnosis is required")
    @Size(max = 1000, message = "Diagnosis is too long")
    private String diagnosis;

    @NotBlank(message = "Medicines are required")
    @Size(max = 2000, message = "Medicines text is too long")
    private String medicines;

    @Size(max = 1000, message = "Advice is too long")
    private String advice;
}