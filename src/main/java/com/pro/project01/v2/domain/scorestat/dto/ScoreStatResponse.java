package com.pro.project01.v2.domain.scorestat.dto;

import com.pro.project01.v2.domain.scorestat.entity.ScoreStat;

import java.time.LocalDateTime;

public record ScoreStatResponse(
        Long id,
        Long userId,

        // 과목/회차 식별 및 표시용
        Long subjectId,
        String subjectName,
        Long roundId,
        Integer roundNumber,

        // 성과 지표
        int totalQuestions,
        int correctAnswers,
        double accuracy,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ScoreStatResponse fromEntity(ScoreStat s) {
        if (s == null) return null;

        var user   = s.getUser();
        var subj   = s.getSubject();
        var round  = s.getRound();

        return new ScoreStatResponse(
                s.getId(),
                user != null ? user.getId() : null,

                subj != null ? subj.getId() : null,
                subj != null ? subj.getName() : null,
                round != null ? round.getId() : null,
                round != null ? round.getRoundNumber() : null,

                s.getTotalQuestions(),
                s.getCorrectAnswers(),
                s.getAccuracy(),

                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
