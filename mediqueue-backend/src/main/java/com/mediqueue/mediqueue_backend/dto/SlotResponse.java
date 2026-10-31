package com.mediqueue.mediqueue_backend.dto;

import com.mediqueue.mediqueue_backend.entity.SlotStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class SlotResponse {
    private Long id;
    private Long doctorId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status;
}