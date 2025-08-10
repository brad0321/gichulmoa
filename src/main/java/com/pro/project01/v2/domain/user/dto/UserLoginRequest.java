package com.pro.project01.v2.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6, max = 100) String password
) {}
