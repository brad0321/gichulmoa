package com.pro.project01.v2.domain.scorestat.dto;

public record ScoreStatRequest(
        Long userId,        // 사용자 ID
        Long subjectId,     // 과목 ID
        String type,        // 문제 유형 (기출문제 / OX퀴즈 → enum)
        Long roundId        // 회차 ID
) {}
