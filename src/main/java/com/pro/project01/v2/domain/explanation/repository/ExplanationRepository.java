package com.pro.project01.v2.domain.explanation.repository;

import com.pro.project01.v2.domain.explanation.dto.ExplanationResponse;
import com.pro.project01.v2.domain.explanation.entity.Explanation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ExplanationRepository extends JpaRepository<Explanation, Long> {

    // ✅ 문제+보기번호로 단건 조회
    Optional<Explanation> findByProblem_IdAndChoiceNo(Long problemId, Integer choiceNo);

    // ✅ 문제의 모든 보기 해설(1~5) 정렬 조회
    List<Explanation> findByProblem_IdOrderByChoiceNoAsc(Long problemId);

    List<ExplanationResponse> findByProblemId(Long problemId);

    // ✅ 없으면 생성, 있으면 내용 갱신/빈값이면 삭제
    @Transactional
    default void upsert(Long problemId, int choiceNo, String content){
        var opt = findByProblem_IdAndChoiceNo(problemId, choiceNo);
        if (content == null || content.isBlank()){
            opt.ifPresent(this::delete);
            return;
        }
        opt.ifPresentOrElse(
                e -> e.setContent(content),
                () -> save(Explanation.builder()
                        .problem(com.pro.project01.v2.domain.problem.entity.Problem.builder().id(problemId).build())
                        .choiceNo(choiceNo)
                        .content(content)
                        .build())
        );
    }
}
