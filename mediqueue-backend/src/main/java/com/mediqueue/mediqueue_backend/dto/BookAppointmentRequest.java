package com.mediqueue.mediqueue_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookAppointmentRequest {
    @NotNull(message = "Slot is required")
    private Long slotId;
    private String reason;
}