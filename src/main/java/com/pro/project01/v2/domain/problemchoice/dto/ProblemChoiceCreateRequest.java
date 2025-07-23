package com.pro.project01.v2.domain.problemchoice.dto;

public record ProblemChoiceCreateRequest(
        Long problemId,
        String content,
        boolean isAnswer
) {}
