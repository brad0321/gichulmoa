package com.pro.project01.v2.domain.problem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    // ✅ 보기 지문
    @Column(name = "view_content", columnDefinition = "TEXT")
    private String viewContent;

    // ✅ 객관식 선택지
    @Column(name = "choice1", columnDefinition = "TEXT")
    private String choice1;

    @Column(name = "choice2", columnDefinition = "TEXT")
    private String choice2;

    @Column(name = "choice3", columnDefinition = "TEXT")
    private String choice3;

    @Column(name = "choice4", columnDefinition = "TEXT")
    private String choice4;

    @Column(name = "choice5", columnDefinition = "TEXT")
    private String choice5;

    // ✅ 외래키: 과목
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // ✅ 외래키: 회차
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    // ✅ 외래키: 목차
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 수정 메서드
    public void update(String title, String content, String imageUrl,
                       String viewContent,
                       String choice1, String choice2, String choice3, String choice4, String choice5,
                       Subject subject, Round round, Unit unit) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.viewContent = viewContent;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.choice5 = choice5;
        this.subject = subject;
        this.round = round;
        this.unit = unit;
        this.updatedAt = LocalDateTime.now();
    }
}
