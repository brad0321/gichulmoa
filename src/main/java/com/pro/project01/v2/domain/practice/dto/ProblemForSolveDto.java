// src/main/java/com/pro/project01/v2/domain/practice/dto/ProblemForSolveDto.java
package com.pro.project01.v2.domain.practice.dto;

import com.pro.project01.v2.domain.problem.entity.Problem;
import java.util.List;

public record ProblemForSolveDto(
        Long itemId,
        String title,
        String viewContent,
        String imageUrl,
        List<String> choices,
        Integer answer
) {
    public static ProblemForSolveDto fromEntity(Problem p) {
        return new ProblemForSolveDto(
                p.getId(),
                p.getTitle(),
                p.getViewContent(),
                p.getImageUrl(),
                List.of(p.getChoice1(), p.getChoice2(), p.getChoice3(), p.getChoice4(), p.getChoice5()),
                p.getAnswer()
        );
    }
}
