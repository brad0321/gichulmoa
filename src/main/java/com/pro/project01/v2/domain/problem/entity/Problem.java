package com.pro.project01.v2.domain.problem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problems")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String viewContent;

    @Column(columnDefinition = "TEXT")
    private String choice1;

    @Column(columnDefinition = "TEXT")
    private String choice2;

    @Column(columnDefinition = "TEXT")
    private String choice3;

    @Column(columnDefinition = "TEXT")
    private String choice4;

    @Column(columnDefinition = "TEXT")
    private String choice5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    private String viewImagePath;

    // ✅ 엔티티 수정 메서드
    public void update(String title, String viewContent, String choice1, String choice2,
                       String choice3, String choice4, String choice5,
                       Subject subject, Round round, Unit unit) {
        this.title = title;
        this.viewContent = viewContent;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.choice5 = choice5;
        this.subject = subject;
        this.round = round;
        this.unit = unit;
    }

    public void updateImagePath(String imagePath) {
        this.viewImagePath = imagePath;
    }
}
