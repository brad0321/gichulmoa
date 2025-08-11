package com.pro.project01.v2.domain.solutionhistory.entity;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solution_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SolutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 문제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    // ✅ 사용자가 선택한 보기를 저장 (1~5)
    private Integer selectedChoiceNo;

    // 정답 여부
    private Boolean isCorrect;

    private LocalDateTime solvedAt;

    @PrePersist
    public void prePersist() {
        this.solvedAt = LocalDateTime.now();
    }

    public void setCorrect(Boolean correct) { this.isCorrect = correct; }
    public void setSelectedChoiceNo(Integer n) { this.selectedChoiceNo = n; }
}
