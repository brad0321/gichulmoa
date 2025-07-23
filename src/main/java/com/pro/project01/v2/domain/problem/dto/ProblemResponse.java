package com.pro.project01.v2.domain.problem.dto;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.entity.Round;
import com.pro.project01.v2.domain.problem.entity.Subject;
import com.pro.project01.v2.domain.problem.entity.Unit;

import java.time.LocalDateTime;

public record ProblemResponse(
        Long id,
        String title,
        String content,
        String imageUrl,
        String viewContent,
        String choice1,
        String choice2,
        String choice3,
        String choice4,
        String choice5,
        Subject subject,
        Round round,
        Unit unit,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProblemResponse from(Problem problem) {
        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getContent(),
                problem.getImageUrl(),
                problem.getViewContent(),
                problem.getChoice1(),
                problem.getChoice2(),
                problem.getChoice3(),
                problem.getChoice4(),
                problem.getChoice5(),
                problem.getSubject(),
                problem.getRound(),
                problem.getUnit(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }

    // ✅ 추가: Thymeleaf에서 사용할 viewImagePath 프로퍼티
    public String getViewImagePath() {
        return imageUrl != null ? imageUrl : null;
    }
}
