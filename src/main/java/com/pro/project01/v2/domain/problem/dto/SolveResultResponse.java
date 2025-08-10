package com.pro.project01.v2.domain.problem.dto;

public record SolveResultResponse(
        boolean correct,
        int answer,
        String explanation
) {}
