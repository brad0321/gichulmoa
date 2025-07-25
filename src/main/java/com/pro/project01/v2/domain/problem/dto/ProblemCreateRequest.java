package com.pro.project01.v2.domain.problem.dto;

public record ProblemCreateRequest(
        String title,
        String viewContent,
        String choice1,
        String choice2,
        String choice3,
        String choice4,
        String choice5,
        Long subjectId,
        Long roundId,
        Long unitId
) {}
