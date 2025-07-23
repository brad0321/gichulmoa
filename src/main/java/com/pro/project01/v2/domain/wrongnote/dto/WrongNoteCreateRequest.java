package com.pro.project01.v2.domain.wrongnote.dto;

public record WrongNoteCreateRequest(
        Long userId,
        Long problemId,
        String memo
) {}
