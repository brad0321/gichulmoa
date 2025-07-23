package com.pro.project01.v2.domain.bookmark.dto;

public record BookmarkCreateRequest(
        Long userId,
        Long problemId,
        String tag
) {}
