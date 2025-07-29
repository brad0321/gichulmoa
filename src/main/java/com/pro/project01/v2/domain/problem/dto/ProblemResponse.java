package com.pro.project01.v2.domain.problem.dto;

import com.pro.project01.v2.domain.problem.entity.Problem;

import java.time.LocalDateTime;

public record ProblemResponse(
        Long id,
        String title,
        String viewContent,
        String imageUrl,
        String choice1,
        String choice2,
        String choice3,
        String choice4,
        String choice5,
        Integer answer,
        String subjectName,
        Integer roundNumber, // ✅ 변경됨
        String unitName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProblemResponse fromEntity(Problem problem) {
        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getViewContent(),
                problem.getImageUrl(),
                problem.getChoice1(),
                problem.getChoice2(),
                problem.getChoice3(),
                problem.getChoice4(),
                problem.getChoice5(),
                problem.getAnswer(),
                problem.getSubject() != null ? problem.getSubject().getName() : null,
                problem.getRound() != null ? problem.getRound().getRoundNumber() : null, // ✅ 변경
                problem.getUnit() != null ? problem.getUnit().getName() : null,
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }
}
