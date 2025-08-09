package com.pro.project01.v2.domain.scorestat.dto;

public record ScoreStatCreateRequest(
        Long userId,           // 사용자 ID
        Long subjectId,        // 과목 ID
        Long roundId,          // 회차 ID
        int totalQuestions,    // 총 문제 수
        int correctAnswers     // 맞힌 문제 수
) {}
