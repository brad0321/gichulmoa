// src/main/java/com/pro/project01/v2/domain/problem/dto/ProblemResponse.java
package com.pro.project01.v2.domain.problem.dto;

import com.pro.project01.v2.domain.explanation.dto.ExplanationResponse;
import com.pro.project01.v2.domain.explanation.entity.Explanation;
import com.pro.project01.v2.domain.problem.entity.Problem;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public record ProblemResponse(
        Long id,

        // 본문/보기/정답/이미지
        String title,
        String viewContent,
        String imageUrl,
        String choice1,
        String choice2,
        String choice3,
        String choice4,
        String choice5,
        Integer answer,

        // 분류
        Long subjectId,
        String subjectName,
        String subjectCode,     // SS (2자리)
        Long roundId,
        Integer roundNumber,    // RR (예: 35)
        String roundName,       // "35회차"
        Long unitId,
        String unitName,
        String unitSeqCode,     // UUU (3자리)

        // 생성/수정
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        // ✅ 문제 전체 해설
        String generalExplanation,

        // 편집용 숫자 필드(엔티티 Byte -> Integer)
        Integer roundProblemNo, // 1~40
        Integer unitProblemNo,  // 1~99

        // 💡 생성컬럼(읽기 전용)
        Integer subjectProblemNo, // 과목 내 고유번호 (29회 기준 시작)
        String roundCode,         // SS_RR_PP
        String unitCode,          // SS_UUU_PP

        // ✅ 보기별 해설 (choiceNo → ExplanationResponse)
        Map<Integer, ExplanationResponse> choiceExplanations
) {

    public static ProblemResponse fromEntity(Problem p) {
        // subject
        Long   subjectId   = p.getSubject() != null ? p.getSubject().getId()   : null;
        String subjectName = p.getSubject() != null ? p.getSubject().getName() : null;
        String subjectCode =
                p.getSubjectCode() != null ? p.getSubjectCode()
                        : (p.getSubject() != null ? p.getSubject().getCode() : null);

        // round
        Long   roundId     = p.getRound() != null ? p.getRound().getId()   : null;
        String roundName   = p.getRound() != null ? p.getRound().getName() : null;
        Integer roundNumber =
                p.getRoundNumber() != null ? p.getRoundNumber().intValue()
                        : (p.getRound() != null && p.getRound().getRoundNumber() != null
                        ? p.getRound().getRoundNumber().intValue()
                        : null);

        // unit
        Long   unitId      = p.getUnit() != null ? p.getUnit().getId()   : null;
        String unitName    = p.getUnit() != null ? p.getUnit().getName() : null;
        String unitSeqCode =
                p.getUnitSeqCode() != null ? p.getUnitSeqCode()
                        : (p.getUnit() != null ? p.getUnit().getSeqCode() : null);

        // numeric converts
        Integer roundProblemNo = p.getRoundProblemNo() != null ? p.getRoundProblemNo().intValue() : null;
        Integer unitProblemNo  = p.getUnitProblemNo()  != null ? p.getUnitProblemNo().intValue()  : null;

        // ✅ 과목 내 고유번호 (Generated Column)
        Integer subjectProblemNo = p.getSubjectProblemNo();

        // ✅ 보기별 해설 Map (choiceNo → ExplanationResponse)
        Map<Integer, ExplanationResponse> choiceExplanations =
                p.getExplanations() == null ? Map.of()
                        : p.getExplanations().stream()
                        // 텍스트/이미지 둘 다 null인 경우는 스킵
                        .filter(e -> e.getContent() != null
                                || e.getImageExplanationUrl() != null)
                        .collect(Collectors.toMap(
                                Explanation::getChoiceNo,
                                ExplanationResponse::from
                        ));

        return new ProblemResponse(
                p.getId(),
                p.getTitle(),
                p.getViewContent(),
                p.getImageUrl(),
                p.getChoice1(),
                p.getChoice2(),
                p.getChoice3(),
                p.getChoice4(),
                p.getChoice5(),
                p.getAnswer(),

                subjectId,
                subjectName,
                subjectCode,
                roundId,
                roundNumber,
                roundName,
                unitId,
                unitName,
                unitSeqCode,

                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getGeneralExplanation(),   // ✅ 변경 포인트

                roundProblemNo,
                unitProblemNo,

                subjectProblemNo,
                p.getRoundCode(),
                p.getUnitCode(),
                choiceExplanations
        );
    }
}
