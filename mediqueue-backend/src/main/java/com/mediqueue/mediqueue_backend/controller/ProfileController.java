package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.ProfileResponse;
import com.mediqueue.mediqueue_backend.dto.UpdateProfileRequest;
import com.mediqueue.mediqueue_backend.entity.User;
import com.mediqueue.mediqueue_backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The logged-in user's own profile (any role). */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping
    public ProfileResponse getProfile(Authentication auth) {
        return toResponse(findUser(auth));
    }

    @PutMapping
    @Transactional
    public ProfileResponse updateProfile(Authentication auth, @Valid @RequestBody UpdateProfileRequest req) {
        User user = findUser(auth);
        user.setName(req.getName().trim());
        String phone = req.getPhone() == null ? "" : req.getPhone().trim();
        user.setPhone(phone.isEmpty() ? null : phone);
        return toResponse(userRepository.save(user));
    }

    private User findUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private ProfileResponse toResponse(User u) {
        return new ProfileResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getRole().name());
    }
}