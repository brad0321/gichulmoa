package com.pro.project01.v2.domain.wrongnote.dto;

import java.time.LocalDateTime;

public record WrongNoteResponse(
        Long id,
        Long userId,
        Long problemId,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

