// src/main/java/com/pro/project01/v2/domain/problem/entity/Problem.java
package com.pro.project01.v2.domain.problem.entity;

import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.unit.entity.Unit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    /** 전체 해설 (추후 Explanation 테이블로 이관 예정) */
    private String explanation;

    /** ✅ 전체 정렬 순서 (DB에 order_no 컬럼 존재) */
    @Column(name = "order_no", nullable = false)
    private int orderNo;

    /* -------------------------------
       코드/번호 관련 필드
       - round_problem_no : 회차 내 문항번호(1~40)
       - round_number     : 회차 표시 숫자 (예: 29, 30)
       - subject_code     : 과목 코드(SS, 예: "01")
       - unit_seq_code    : 단원 코드(UUU, 예: "003")
       - unit_problem_no  : 단원 내 번호(1~99)
       - round_code       : SS_RR_PP (예: 01_29_01)
       - unit_code        : SS_UUU_PP(예: 01_001_01)
       - subject_problem_no : (29회 기준) 과목 내 고유번호
                              ( (round_number-29)*40 + round_problem_no )
       -------------------------------- */

    /** 회차 내 문항번호: 1~40 (NULL 허용 → Byte 래퍼) */
    @Column(name = "round_problem_no")
    private Byte roundProblemNo;

    /** 회차 표시 숫자(RR) — round FK로부터 동기화 */
    @Column(name = "round_number")
    private Short roundNumber;

    /** 과목 코드(SS: 2자리, 예: 01=민법) */
    @Column(name = "subject_code", length = 2)
    private String subjectCode;

    /** 단원 코드(UUU: 3자리, 예: 001) */
    @Column(name = "unit_seq_code", length = 3)
    private String unitSeqCode;

    /** 단원 내 문제번호: 1~99 → PP */
    @Column(name = "unit_problem_no")
    private Byte unitProblemNo;

    /** 생성컬럼: SS_RR_PP (예: 01_35_12) → 길이 9 */
    @Column(name = "round_code", length = 9, insertable = false, updatable = false)
    private String roundCode;

    /** 생성컬럼: SS_UUU_PP (예: 01_003_07) → 길이 10 */
    @Column(name = "unit_code", length = 10, insertable = false, updatable = false)
    private String unitCode;

    /** ✅ 생성컬럼: 과목 내 고유번호 (29회 기준 시작)
     *   subject_problem_no = (round_number - 29) * 40 + round_problem_no
     *   - DB Generated Column 이므로 읽기 전용으로 매핑
     */
    @Column(name = "subject_problem_no", insertable = false, updatable = false)
    private Integer subjectProblemNo;

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
       업데이트 메서드
       - 생성컬럼(round_code, unit_code, subject_problem_no)은 직접 건드리지 않음
       - 폼에서 채운 구성요소들만 반영
       =============================== */
    public void update(
            String title, String viewContent, String imageUrl,
            String choice1, String choice2, String choice3,
            String choice4, String choice5, Integer answer,
            Subject subject, Round round, Unit unit,

            // 코드/번호 관련 신규 필드
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

        // FK에서 코드/번호 다시 동기화 (필요 시)
        syncCodesFromFKs();
    }

    /** 정렬 순서 교체용 */
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    /** 코드 구성요소만 따로 갱신 (생성컬럼은 DB에서 자동 생성) */
    public void updateCodes(Byte roundProblemNo, String subjectCode, String unitSeqCode, Byte unitProblemNo) {
        this.roundProblemNo = roundProblemNo;
        this.subjectCode    = subjectCode;    // 길이 2
        this.unitSeqCode    = unitSeqCode;    // 길이 3
        this.unitProblemNo  = unitProblemNo;
    }

    /** FK로부터 코드/번호 구성요소 동기화
     *  - subject.code        → subjectCode
     *  - round.roundNumber   → roundNumber
     *  - unit.seqCode        → unitSeqCode
     */
    public void syncCodesFromFKs() {
        if (this.subject != null && this.subject.getCode() != null) {
            this.subjectCode = this.subject.getCode();      // 예: "01"
        }
        if (this.round != null && this.round.getRoundNumber() != null) {
            this.roundNumber = this.round.getRoundNumber(); // 예: 29
        }
        if (this.unit != null && this.unit.getSeqCode() != null) {
            this.unitSeqCode = this.unit.getSeqCode();      // 예: "003"
        }
    }
}
