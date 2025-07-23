package com.pro.project01.v2.domain.memo.dto;

import java.time.LocalDateTime;

public record MemoResponse(
        Long id,
        Long userId,
        Long problemId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
