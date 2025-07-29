package com.pro.project01.v2.domain.user.dto;

import com.pro.project01.v2.domain.user.entity.Grade;
import com.pro.project01.v2.domain.user.entity.Role;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        Grade grade,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
