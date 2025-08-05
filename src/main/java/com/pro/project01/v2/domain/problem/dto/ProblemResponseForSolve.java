package com.pro.project01.v2.domain.problem.dto;

import java.util.List;

public record ProblemResponseForSolve(
        Long id,
        String title,
        String viewContent,
        String imageUrl,
        List<ChoiceDto> choices
) {
    public record ChoiceDto(String content) {}
}
