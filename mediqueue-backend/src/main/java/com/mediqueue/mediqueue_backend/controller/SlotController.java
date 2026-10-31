package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.CreateSlotRequest;
import com.mediqueue.mediqueue_backend.dto.SlotResponse;
import com.mediqueue.mediqueue_backend.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping("/doctor/slots")
    public ResponseEntity<List<SlotResponse>> create(@Valid @RequestBody CreateSlotRequest req,
                                                     Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(slotService.createSlots(auth.getName(), req));
    }

    @GetMapping("/doctors/{doctorId}/slots")
    public List<SlotResponse> available(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return slotService.availableSlots(doctorId, date);
    }
}