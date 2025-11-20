package com.pro.project01.v2.domain.explanation.entity;

import com.pro.project01.v2.domain.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "explanation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_explanation_problem_choice",
                        columnNames = {"problem_id", "choice_no"}
                )
        }
)
public class Explanation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 보기 번호(1~5) */
    @Column(name = "choice_no")
    private Integer choiceNo;

    /** 텍스트 해설 (틀린 보기만, null 허용) */
    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = true)
    private String content;

    /** ✅ 이미지 해설 (S3 URL 또는 key, null 허용) */
    @Column(name = "image_explanation_url", length = 500, nullable = true)
    private String imageExplanationUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    /** ✅ 텍스트 해설 수정 */
    public void changeContent(String content) {
        this.content = content;
    }

    /** ✅ 이미지 해설 수정 */
    public void changeImageExplanationUrl(String imageExplanationUrl) {
        this.imageExplanationUrl = imageExplanationUrl;
    }

    /** Problem 연관관계 설정 (패키지 내부에서만 사용) */
    void changeProblem(Problem problem) {
        this.problem = problem;
    }
}
