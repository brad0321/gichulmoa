package com.pro.project01.v2.domain.explanation.dto;

import com.pro.project01.v2.domain.explanation.entity.Explanation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExplanationResponse {
    private Long id;
    private Long problemId;
    private String content;

    public static ExplanationResponse from(Explanation explanation) {
        return ExplanationResponse.builder()
                .id(explanation.getId())
                .problemId(explanation.getProblem().getId())
                .content(explanation.getContent())
                .build();
    }
}
