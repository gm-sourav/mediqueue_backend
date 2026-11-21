package com.mediqueue.mediqueue_backend.controller;

import com.mediqueue.mediqueue_backend.dto.ProfileResponse;
import com.mediqueue.mediqueue_backend.dto.UpdateProfileRequest;
import com.mediqueue.mediqueue_backend.entity.User;
import com.mediqueue.mediqueue_backend.repository.UserRepository;
import com.mediqueue.mediqueue_backend.service.PhotoStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** The logged-in user's own profile (any role). */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final PhotoStorageService photoStorage;

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
        String gender = req.getGender() == null ? "" : req.getGender().trim();
        user.setGender(gender.isEmpty() ? null : gender);
        return toResponse(userRepository.save(user));
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ProfileResponse uploadPhoto(Authentication auth, @RequestParam("file") MultipartFile file) {
        User user = findUser(auth);
        String old = user.getPhotoUrl();
        user.setPhotoUrl(photoStorage.save(file));
        userRepository.save(user);
        photoStorage.delete(old);
        return toResponse(user);
    }

    @DeleteMapping("/photo")
    @Transactional
    public ProfileResponse removePhoto(Authentication auth) {
        User user = findUser(auth);
        photoStorage.delete(user.getPhotoUrl());
        user.setPhotoUrl(null);
        return toResponse(userRepository.save(user));
    }

    private User findUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private ProfileResponse toResponse(User u) {
        return new ProfileResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getRole().name(), u.getGender(), u.getPhotoUrl());
    }
}
