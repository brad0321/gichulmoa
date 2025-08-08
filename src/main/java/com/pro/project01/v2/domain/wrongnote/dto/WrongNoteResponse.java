package com.pro.project01.v2.domain.wrongnote.dto;

import com.pro.project01.v2.domain.wrongnote.entity.WrongNote;
import java.time.LocalDateTime;

public record WrongNoteResponse(
        Long id,
        Long userId,
        Long problemId,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static WrongNoteResponse from(WrongNote wn) {
        return new WrongNoteResponse(
                wn.getId(),
                wn.getUser().getId(),
                wn.getProblem().getId(),
                wn.getMemo(),
                wn.getCreatedAt(),
                wn.getUpdatedAt()
        );
    }
}
