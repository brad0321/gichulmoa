package com.pro.project01.v2.domain.solutionhistory.dto;

public record SolutionHistoryCreateRequest(
        Long userId,
        Long problemId,
        Boolean isCorrect
) {}
