package com.sachin.documind.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Username is required") @Size(min = 3, max = 20) String username,
        @NotBlank(message = "Email is required") @Email String email,
        @NotBlank(message = "Password is required") @Size(min = 6) String password
) {}
