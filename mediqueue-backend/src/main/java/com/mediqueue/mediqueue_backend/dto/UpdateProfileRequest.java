package com.mediqueue.mediqueue_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name is too long")
    private String name;

    // Empty is allowed (phone is optional)
    @Pattern(regexp = "^$|^[0-9+\\-() ]{6,20}$", message = "Invalid phone number")
    private String phone;
}