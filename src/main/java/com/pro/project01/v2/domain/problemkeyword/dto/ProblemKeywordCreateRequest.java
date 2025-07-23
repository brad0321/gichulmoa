package com.pro.project01.v2.domain.problemkeyword.dto;

public record ProblemKeywordCreateRequest(
        Long problemId,
        String keyword
) {}
