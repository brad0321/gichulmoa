package com.pro.project01.v2.domain.bookmark.dto;

import java.time.LocalDateTime;

public record BookmarkResponse(
        Long id,
        Long userId,
        Long problemId,
        String tag,
        LocalDateTime createdAt
) {}
