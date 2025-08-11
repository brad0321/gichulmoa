package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.explanation.repository.ExplanationRepository;
import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// 계산문제 마킹용 리포지토리 (필요시 사용)
import com.pro.project01.v2.domain.calculation.repository.CalculationProblemRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;
    private final CalculationProblemRepository calculationProblemRepository; // 필요 시 활용
    private final ExplanationRepository explanationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProblemResponse> findAll() {
        return problemRepository.findAll().stream()
                .map(ProblemResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemResponse findById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));
        return ProblemResponse.fromEntity(problem);
    }

    /**
     * 생성: imagePath 가 null/blank면 이미지 없음으로 저장
     */
    @Override
    public Long create(ProblemRequest request, String imagePath) {
        Problem problem = Problem.builder()
                .title(trimOrNull(request.title()))
                .viewContent(trimOrNull(request.viewContent()))
                .imageUrl(blankToNull(imagePath))
                .choice1(trimOrNull(request.choice1()))
                .choice2(trimOrNull(request.choice2()))
                .choice3(trimOrNull(request.choice3()))
                .choice4(trimOrNull(request.choice4()))
                .choice5(trimOrNull(request.choice5()))
                .answer(request.answer())
                .subject(subjectRepository.findById(request.subjectId())
                        .orElseThrow(() -> new IllegalArgumentException("과목을 찾을 수 없습니다.")))
                .round(roundRepository.findById(request.roundId())
                        .orElseThrow(() -> new IllegalArgumentException("회차를 찾을 수 없습니다.")))
                .unit(unitRepository.findById(request.unitId())
                        .orElseThrow(() -> new IllegalArgumentException("목차를 찾을 수 없습니다.")))
                .build();

        problemRepository.save(problem);

        // (옵션) 계산문제 테이블 마킹 훅 - 기준 확정되면 활성화
        // if (isCalculationProblem(problem)) {
        //     calculationProblemRepository.save(CalculationProblem.builder().problem(problem).build());
        // }

        return problem.getId();
    }

    /**
     * 업데이트: 이미지 처리 규칙 적용
     * - imagePath == null  -> 이미지 변경 없음(기존 유지)
     * - imagePath == ""    -> 이미지 제거(null 로 세팅)
     * - imagePath == "..." -> 해당 파일명으로 교체
     */
    @Override
    public void update(Long id, ProblemRequest request, String imagePath) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));

        String finalImageUrl = resolveImageUrlForUpdate(problem.getImageUrl(), imagePath);

        problem.update(
                trimOrNull(request.title()),
                trimOrNull(request.viewContent()),
                finalImageUrl,
                trimOrNull(request.choice1()),
                trimOrNull(request.choice2()),
                trimOrNull(request.choice3()),
                trimOrNull(request.choice4()),
                trimOrNull(request.choice5()),
                request.answer(),
                subjectRepository.findById(request.subjectId())
                        .orElseThrow(() -> new IllegalArgumentException("과목을 찾을 수 없습니다.")),
                roundRepository.findById(request.roundId())
                        .orElseThrow(() -> new IllegalArgumentException("회차를 찾을 수 없습니다.")),
                unitRepository.findById(request.unitId())
                        .orElseThrow(() -> new IllegalArgumentException("목차를 찾을 수 없습니다."))
        );
    }

    @Override
    public void delete(Long id) {
        problemRepository.deleteById(id);
        // 필요하다면 해설/계산문제 등 연관 정리 로직 추가 (FK ON DELETE CASCADE 또는 JPA cascade로 처리 권장)
    }

    // ===== 확장 시그니처 (컨트롤러에서 사용) =====

    @Override
    public Long create(ProblemRequest request, String imagePath, List<String> exps){
        Long problemId = create(request, imagePath);
        upsertChoiceExplanations(problemId, exps);
        return problemId;
    }

    @Override
    public void update(Long id, ProblemRequest request, String imagePath, List<String> exps){
        update(id, request, imagePath);
        upsertChoiceExplanations(id, exps);
    }

    // ===== 내부 유틸 =====

    private void upsertChoiceExplanations(Long problemId, List<String> exps){
        if (exps == null) return;
        for (int i = 0; i < 5; i++){
            String c = exps.size() > i ? trimOrNull(exps.get(i)) : null;
            explanationRepository.upsert(problemId, i + 1, c);
        }
    }

    private static String resolveImageUrlForUpdate(String current, String imagePathParam){
        if (imagePathParam == null) {
            // 변경 없음
            return current;
        }
        if (imagePathParam.isEmpty()) {
            // 삭제
            return null;
        }
        // 교체
        return imagePathParam;
    }

    private static String trimOrNull(String s){
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String blankToNull(String s){
        return (s == null || s.isBlank()) ? null : s;
    }

    // 기준 확정 시 사용
    // private static boolean isCalculationProblem(Problem p){
    //     String t = Objects.toString(p.getTitle(), "") + " " + Objects.toString(p.getViewContent(), "");
    //     return t.contains("계산") || t.contains("합계") || t.matches(".*\\d+\\s*[×x+\\-\\/].*");
    // }
}
