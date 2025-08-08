package com.pro.project01.v2.domain.problem.dto;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.entity.ProblemType;

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
        ProblemType type, // ✅ 새로 추가된 필드
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
                problem.getSubject() != null ? problem.getSubject().getId() : null,
                problem.getSubject() != null ? problem.getSubject().getName() : null,
                problem.getRound() != null ? problem.getRound().getId() : null,
                problem.getRound() != null ? problem.getRound().getRoundNumber() : null,
                problem.getUnit() != null ? problem.getUnit().getId() : null,
                problem.getUnit() != null ? problem.getUnit().getName() : null,
                problem.getType(), // ✅ enum 값 그대로 전달
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }
}
