package com.pro.project01.v2.domain.problem.dto;

public record ProblemRequest(
        String title,
        String viewContent,
        String choice1,
        String choice2,
        String choice3,
        String choice4,
        String choice5,
        Integer answer,
        Long subjectId,
        Long roundId,
        Long unitId,
        String type // ✅ 새로 추가 (enum 이름을 문자열로 받음)
) {}
