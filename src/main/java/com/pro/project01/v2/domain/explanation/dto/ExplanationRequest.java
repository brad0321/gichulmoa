package com.pro.project01.v2.domain.explanation.dto;

public record ExplanationRequest(
        Integer choiceNo,
        String content,
        String imageExplanationUrl
) {}
