package com.pro.project01.v2.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password
) { }
