package com.pro.project01.v2.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Email String email,
        @Size(min = 6, max = 100) String newPassword
) {}
