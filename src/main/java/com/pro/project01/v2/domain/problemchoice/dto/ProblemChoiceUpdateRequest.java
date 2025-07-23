package com.pro.project01.v2.domain.problemchoice.dto;

public record ProblemChoiceUpdateRequest(
        String content,
        boolean isAnswer
) {}
