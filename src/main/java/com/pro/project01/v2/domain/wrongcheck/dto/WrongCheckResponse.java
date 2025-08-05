package com.pro.project01.v2.domain.wrongcheck.dto;

import java.time.LocalDateTime;
import com.pro.project01.v2.domain.wrongcheck.entity.WrongCheck;

public record WrongCheckResponse(
        Long id,
        Long userId,
        Long problemId,
        boolean correct,
        String tag,
        LocalDateTime createdAt
) {
    /**
     * 엔티티 → DTO 변환용 정적 팩토리
     */
    public static WrongCheckResponse from(WrongCheck e) {
        return new WrongCheckResponse(
                e.getId(),
                e.getUser().getId(),      // userId
                e.getProblem().getId(),   // problemId
                e.isCorrect(),            // 정답 여부
                e.getTag(),
                e.getCreatedAt()
        );
    }
}
