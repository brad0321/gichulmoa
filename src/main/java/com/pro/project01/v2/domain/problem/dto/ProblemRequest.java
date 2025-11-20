// src/main/java/com/pro/project01/v2/domain/problem/dto/ProblemRequest.java
package com.pro.project01.v2.domain.problem.dto;

public record ProblemRequest(
        String title,
        String viewContent,
        String generalExplanation,   // ✅ 문제 전체 해설

        String choice1,
        String choice2,
        String choice3,
        String choice4,
        String choice5,

        Integer answer,
        Long subjectId,
        Long roundId,
        Long unitId,

        Integer roundProblemNo,
        String subjectCode,
        String unitSeqCode,
        Integer unitProblemNo
) {}
