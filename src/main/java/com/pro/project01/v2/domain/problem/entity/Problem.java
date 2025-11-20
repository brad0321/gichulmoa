// src/main/java/com/pro/project01/v2/domain/problem/entity/Problem.java
package com.pro.project01.v2.domain.problem.entity;

import com.pro.project01.v2.domain.explanation.entity.Explanation;
import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.unit.entity.Unit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "problem")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 제목 */
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String title;

    /** 보기 지문 */
    @Column(name = "view_content", columnDefinition = "TEXT")
    private String viewContent;

    /** 이미지 경로 */
    @Column(name = "image_url")
    private String imageUrl;

    /** 선택지 */
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String choice5;

    /** 정답(1~5) */
    @Column(nullable = false)
    private Integer answer;

    /** 연관관계 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    /** 생성/수정시각 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** ✅ 문제 전체 해설 (1문제당 1개, null 허용)
     *  - DB 컬럼 이름은 그대로 explanation 사용
     */
    @Lob
    @Column(name = "explanation", columnDefinition = "LONGTEXT", nullable = true)
    private String generalExplanation;

    /** ✅ 전체 정렬 순서 (DB에 order_no 컬럼 존재) */
    @Column(name = "order_no", nullable = false)
    private int orderNo;

    /* -------------------------------
       코드/번호 관련 필드 (생략 주석 그대로 유지)
       -------------------------------- */

    @Column(name = "round_problem_no")
    private Byte roundProblemNo;

    @Column(name = "round_number")
    private Short roundNumber;

    @Column(name = "subject_code", length = 2)
    private String subjectCode;

    @Column(name = "unit_seq_code", length = 3)
    private String unitSeqCode;

    @Column(name = "unit_problem_no")
    private Byte unitProblemNo;

    @Column(name = "round_code", length = 9, insertable = false, updatable = false)
    private String roundCode;

    @Column(name = "unit_code", length = 10, insertable = false, updatable = false)
    private String unitCode;

    @Column(name = "subject_problem_no", insertable = false, updatable = false)
    private Integer subjectProblemNo;

    /** ✅ 보기별 해설들 (1:N)
     *  - 틀린 보기 중, 해설 있는 보기들만 Explanation row 생성
     */
    @Builder.Default
    @OneToMany(mappedBy = "problem",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Explanation> explanations = new ArrayList<>();

    /* ===============================
       라이프사이클 콜백
       =============================== */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ===============================
       업데이트 메서드 (기존 그대로 유지)
       =============================== */
    public void update(
            String title, String viewContent, String imageUrl,
            String choice1, String choice2, String choice3,
            String choice4, String choice5, Integer answer,
            Subject subject, Round round, Unit unit,

            Byte roundProblemNo,
            String subjectCode, String unitSeqCode, Byte unitProblemNo,
            Integer orderNo
    ) {
        this.title = title;
        this.viewContent = viewContent;
        this.imageUrl = imageUrl;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.choice5 = choice5;
        this.answer = answer;

        this.subject = subject;
        this.round = round;
        this.unit = unit;

        this.roundProblemNo = roundProblemNo;
        this.subjectCode    = subjectCode;   // 2자리 SS
        this.unitSeqCode    = unitSeqCode;   // 3자리 UUU
        this.unitProblemNo  = unitProblemNo;

        if (orderNo != null) {
            this.orderNo = orderNo;
        }

        syncCodesFromFKs();
    }

    /** 정렬 순서 교체용 */
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public void updateCodes(Byte roundProblemNo, String subjectCode, String unitSeqCode, Byte unitProblemNo) {
        this.roundProblemNo = roundProblemNo;
        this.subjectCode    = subjectCode;    // 길이 2
        this.unitSeqCode    = unitSeqCode;    // 길이 3
        this.unitProblemNo  = unitProblemNo;
    }

    public void syncCodesFromFKs() {
        if (this.subject != null && this.subject.getCode() != null) {
            this.subjectCode = this.subject.getCode();
        }
        if (this.round != null && this.round.getRoundNumber() != null) {
            this.roundNumber = this.round.getRoundNumber();
        }
        if (this.unit != null && this.unit.getSeqCode() != null) {
            this.unitSeqCode = this.unit.getSeqCode();
        }
    }

    /* ===============================
       ✅ 해설 관련 도메인 메서드 (세터 X)
       =============================== */

    /** 문제 전체 해설 변경 */
    public void changeGeneralExplanation(String generalExplanation) {
        this.generalExplanation = generalExplanation;
    }

    /**
     * 보기 해설 등록/수정 (Map 느낌으로 사용)
     * - content가 null 또는 빈문자면 해당 보기 해설 삭제
     * - 선택적으로 틀린 보기만 넣으면 됨
     */
    public void putChoiceExplanation(Integer choiceNo, String content) {
        if (choiceNo == null) return;

        Explanation existing = this.explanations.stream()
                .filter(e -> Objects.equals(e.getChoiceNo(), choiceNo))
                .findFirst()
                .orElse(null);

        // content 비어있으면 삭제
        if (content == null || content.isBlank()) {
            if (existing != null) {
                this.explanations.remove(existing);
            }
            return;
        }

        // 수정
        if (existing != null) {
            existing.changeContent(content);
            return;
        }

        // 신규 등록
        Explanation explanation = Explanation.builder()
                .choiceNo(choiceNo)
                .content(content)
                .problem(this)
                .build();

        this.explanations.add(explanation);
    }

    /** 보기 해설 전체 삭제 (예: 문제 해설 초기화 등) */
    public void clearChoiceExplanations() {
        this.explanations.clear();
    }

    /**
     * 읽기용: 보기번호 → 해설내용 Map 으로 변환
     * - 서비스/컨트롤러 단에서 map 방식으로 편하게 사용
     */
    public Map<Integer, String> getChoiceExplanationMap() {
        return this.explanations.stream()
                .filter(e -> e.getContent() != null && !e.getContent().isBlank())
                .collect(Collectors.toMap(
                        Explanation::getChoiceNo,
                        Explanation::getContent
                ));
    }
}
