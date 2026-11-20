package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.AuthResponse;
import com.mediqueue.mediqueue_backend.dto.ForgotPasswordRequest;
import com.mediqueue.mediqueue_backend.dto.LoginRequest;
import com.mediqueue.mediqueue_backend.dto.RegisterRequest;
import com.mediqueue.mediqueue_backend.dto.ResetPasswordRequest;
import com.mediqueue.mediqueue_backend.dto.UserResponse;
import com.mediqueue.mediqueue_backend.service.AuthService;
import com.mediqueue.mediqueue_backend.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerPatient(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        passwordResetService.requestReset(req.getEmail());
        return ResponseEntity.ok(Map.of("message", "If this email is registered, a code has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        passwordResetService.resetPassword(req.getEmail(), req.getCode(), req.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed"));
    }
}