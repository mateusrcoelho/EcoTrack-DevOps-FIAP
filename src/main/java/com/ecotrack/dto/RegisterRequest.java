package com.ecotrack.dto;

import com.ecotrack.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String nome,
        @Email @NotBlank String email,
        @Size(min = 6) String senha,
        Role role
) {}
