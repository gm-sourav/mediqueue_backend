package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.QueueResponse;
import com.mediqueue.mediqueue_backend.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PutMapping("/doctor/queue/next")
    public QueueResponse next(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication auth) {
        return queueService.next(auth.getName(), date);
    }

    @GetMapping("/doctor/queue")
    public QueueResponse myQueue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication auth) {
        return queueService.myQueue(auth.getName(), date);
    }

    @GetMapping("/doctors/{doctorId}/queue")
    public QueueResponse doctorQueue(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return queueService.viewForDoctor(doctorId, date);
    }
}