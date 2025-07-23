package com.pro.project01.v2.domain.memo.dto;

public record MemoCreateRequest(
        Long userId,
        Long problemId,
        String content
) {}
