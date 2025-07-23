package com.pro.project01.v2.domain.problemchoice.dto;

public record ProblemChoiceResponse(
        Long id,
        Long problemId,
        String content,
        boolean isAnswer
) {}
