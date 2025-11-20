// src/main/java/com/pro/project01/v2/domain/problem/dto/ProblemListItemDto.java
package com.pro.project01.v2.domain.problem.dto;

import java.time.LocalDateTime;

public record ProblemListItemDto(
        Long id,
        String title,
        String subjectName,
        String roundName,
        Integer roundNumber,
        String unitName,
        Integer roundProblemNo,
        Integer subjectProblemNo,
        LocalDateTime createdAt
) {
    public static ProblemListItemDto from(ProblemListItemView v) {
        return new ProblemListItemDto(
                v.getId(),
                v.getTitle(),
                v.getSubjectName(),
                v.getRoundName(),
                v.getRoundNumber(),
                v.getUnitName(),
                v.getRoundProblemNo(),
                v.getSubjectProblemNo(),
                v.getCreatedAt()
        );
    }
}
