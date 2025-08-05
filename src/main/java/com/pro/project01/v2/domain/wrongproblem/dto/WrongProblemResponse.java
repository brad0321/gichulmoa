package com.pro.project01.v2.domain.wrongproblem.dto;

import java.time.LocalDateTime;

public record WrongProblemResponse(
        Long id,
        Long userId,
        Long problemId,
        String reason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
