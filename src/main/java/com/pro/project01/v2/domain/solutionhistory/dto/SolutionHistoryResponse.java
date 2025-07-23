package com.pro.project01.v2.domain.solutionhistory.dto;

import java.time.LocalDateTime;

public record SolutionHistoryResponse(
        Long id,
        Long userId,
        Long problemId,
        Boolean isCorrect,
        LocalDateTime solvedAt
) {}
