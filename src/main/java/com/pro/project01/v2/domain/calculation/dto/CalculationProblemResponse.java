package com.pro.project01.v2.domain.calculation.dto;

import java.time.LocalDateTime;

public record CalculationProblemResponse(
        Long id,
        Long problemId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
