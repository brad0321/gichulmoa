package com.pro.project01.v2.domain.problem.dto;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.entity.Round;
import com.pro.project01.v2.domain.problem.entity.Subject;
import com.pro.project01.v2.domain.problem.entity.Unit;

public record ProblemResponse(
        Long id,
        String title,
        String viewImagePath, // ✅ 이미지 경로
        String viewContent,
        String choice1,
        String choice2,
        String choice3,
        String choice4,
        String choice5,
        Subject subject,
        Round round,
        Unit unit
) {
    public static ProblemResponse from(Problem problem) {
        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getViewImagePath(), // ✅ 이미지 파일명
                problem.getViewContent(),
                problem.getChoice1(),
                problem.getChoice2(),
                problem.getChoice3(),
                problem.getChoice4(),
                problem.getChoice5(),
                problem.getSubject(),
                problem.getRound(),
                problem.getUnit()
        );
    }

    // ✅ Thymeleaf에서 이미지 경로 접근용
    public String getImageUrl() {
        return viewImagePath != null ? "/uploads/" + viewImagePath : null;
    }
}
