package com.pro.project01.v2.domain.scorestat.dto;

import java.time.LocalDateTime;

public record ScoreStatResponse(
        Long id,
        Long userId,
        String subjectName,
        Integer roundNumber,     // ✅ String → Integer (명확하게 변경)
        int totalQuestions,
        int correctAnswers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
