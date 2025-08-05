package com.pro.project01.v2.domain.wrongproblem.dto;

public record WrongProblemCreateRequest(
        Long userId,
        Long problemId,
        String reason // ✅ 추가됨
) {
}
