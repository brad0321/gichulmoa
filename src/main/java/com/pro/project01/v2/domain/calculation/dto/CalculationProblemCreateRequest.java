package com.pro.project01.v2.domain.calculation.dto;

public record CalculationProblemCreateRequest(
        Long problemId,    // ✅ 연결할 Problem ID
        String formula     // ✅ 계산식 or 풀이 내용
) {}
