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
        Long subjectId,
        String subjectName,
        Long roundId,
        Integer roundNumber, // ✅ Round -> roundNumber
        Long unitId,
        String unitName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String explanation
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
                problem.getSubject() != null ? problem.getSubject().getId() : null,
                problem.getSubject() != null ? problem.getSubject().getName() : null,
                problem.getRound() != null ? problem.getRound().getId() : null,
                problem.getRound() != null ? problem.getRound().getRoundNumber() : null,
                problem.getUnit() != null ? problem.getUnit().getId() : null,
                problem.getUnit() != null ? problem.getUnit().getName() : null,
                problem.getCreatedAt(),
                problem.getUpdatedAt(),
                problem.getExplanation()
        );
    }
}
