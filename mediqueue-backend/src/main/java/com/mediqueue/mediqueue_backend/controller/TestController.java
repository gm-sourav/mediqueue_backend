package com.mediqueue.mediqueue_backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/me")
    public Map<String, String> me(Authentication auth) {
        return Map.of(
                "email", auth.getName(),
                "role", auth.getAuthorities().iterator().next().getAuthority());
    }

    @GetMapping("/admin/ping")
    public Map<String, String> adminPing() {
        return Map.of("message", "Hello Admin");
    }
}