package com.pro.project01.v2.domain.user.dto;

import com.pro.project01.v2.domain.user.entity.Grade;
import com.pro.project01.v2.domain.user.entity.Role;

public record UserResponse(
        Long id,
        String username,
        String email,
        Grade grade,
        Role role
) {}
