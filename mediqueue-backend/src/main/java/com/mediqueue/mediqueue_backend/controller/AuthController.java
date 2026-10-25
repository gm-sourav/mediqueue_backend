package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.RegisterRequest;
import com.mediqueue.mediqueue_backend.dto.UserResponse;
import com.mediqueue.mediqueue_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerPatient(req));
    }
}