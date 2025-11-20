// src/main/java/com/pro/project01/v2/domain/explanation/dto/ExplanationResponse.java
package com.pro.project01.v2.domain.explanation.dto;

import com.pro.project01.v2.domain.explanation.entity.Explanation;

public record ExplanationResponse(
        Integer choiceNo,
        String content,
        String imageExplanationUrl
) {
    public static ExplanationResponse from(Explanation e) {
        if (e == null) return null;
        return new ExplanationResponse(
                e.getChoiceNo(),
                e.getContent(),
                e.getImageExplanationUrl()
        );
    }
}
