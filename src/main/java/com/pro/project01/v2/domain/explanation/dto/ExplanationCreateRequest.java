package com.pro.project01.v2.domain.explanation.dto;

public record ExplanationCreateRequest(
        Long problemId,
        Integer choiceNo,
        String content
) {}
