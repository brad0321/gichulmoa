package com.pro.project01.v2.domain.scorestat.entity;

import com.pro.project01.v2.domain.problem.entity.ProblemType;
import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "score_stats",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id","subject_id","round_id","type"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScoreStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemType type;

    private int totalQuestions;   // 총 문제 수
    private int correctAnswers;   // 맞힌 문제 수
    private double accuracy;         // 정답률(%)

    /** 문제 하나 제출할 때마다 호출 */
    public void record(boolean isCorrect) {
        this.totalQuestions++;
        if (isCorrect) this.correctAnswers++;
        this.accuracy =
                Math.round((double) correctAnswers / totalQuestions * 1000) / 10.0; // 소수 1자리
    }

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 점수 업데이트
    public void updateScore(int totalQuestions, int correctAnswers) {
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.updatedAt = LocalDateTime.now();
    }
}
