package com.mediqueue.mediqueue_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class QueueResponse {
    private Long doctorId;
    private String doctorName;
    private LocalDate queueDate;
    private Integer currentToken;
    private Long waitingCount;
}