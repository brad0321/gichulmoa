package com.pro.project01.v2.domain.scorestat.dto;

public record ScoreStatUpdateRequest(
        int totalQuestions,   // 총 문제 수
        int correctAnswers    // 맞힌 문제 수
) {}
