package com.pro.project01.v2.domain.problemchoice.entity;

import com.pro.project01.v2.domain.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProblemChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isAnswer;

    public void updateContent(String content) {
        this.content = content;
    }

    public void markAsAnswer(boolean isAnswer) {
        this.isAnswer = isAnswer;
    }
}
