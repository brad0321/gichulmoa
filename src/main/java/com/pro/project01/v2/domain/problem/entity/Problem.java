package com.pro.project01.v2.domain.problem.entity;

import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.unit.entity.Unit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 문제 제목

    @Lob
    private String viewContent; // 보기 지문 (텍스트)

    private String imageUrl; // 이미지 경로

    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String choice5;

    @Column(nullable = false)
    private Integer answer; // 정답 (1~5)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

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
}
