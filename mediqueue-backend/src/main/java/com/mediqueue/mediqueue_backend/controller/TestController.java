package com.mediqueue.mediqueue_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Only the admin ping is left here. GET /api/me now lives in ProfileController. */
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/admin/ping")
    public Map<String, String> adminPing() {
        return Map.of("message", "Hello Admin");
    }
}