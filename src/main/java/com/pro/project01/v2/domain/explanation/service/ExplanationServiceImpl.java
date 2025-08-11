package com.pro.project01.v2.domain.explanation.service;

import com.pro.project01.v2.domain.explanation.dto.ExplanationCreateRequest;
import com.pro.project01.v2.domain.explanation.dto.ExplanationResponse;
import com.pro.project01.v2.domain.explanation.dto.ExplanationUpdateRequest;
import com.pro.project01.v2.domain.explanation.entity.Explanation;
import com.pro.project01.v2.domain.explanation.repository.ExplanationRepository;
import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExplanationServiceImpl implements ExplanationService {

    private final ExplanationRepository explanationRepository;
    private final ProblemRepository problemRepository;

    @Override
    public List<ExplanationResponse> findByProblemId(Long problemId) {
        // ❗ 레포 메서드: findByProblem_IdOrderByChoiceNoAsc 로 맞춰 사용
        return explanationRepository.findByProblem_IdOrderByChoiceNoAsc(problemId)
                .stream()
                .map(ExplanationResponse::from)   // from(Explanation e) 시그니처와 일치
                .toList();
    }

    @Override
    public ExplanationResponse findById(Long id) {
        Explanation explanation = explanationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해설을 찾을 수 없습니다."));
        return ExplanationResponse.from(explanation);
    }

    @Override
    public void create(ExplanationCreateRequest request) {
        // ⚠️ new Explanation() (protected) 금지 → builder 사용
        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));

        Explanation explanation = Explanation.builder()
                .problem(problem)
                .choiceNo(request.getChoiceNo())   // 보기 번호가 있는 DTO 전제
                .content(request.getContent())
                .build();

        explanationRepository.save(explanation);
    }

    @Override
    public void update(Long id, ExplanationUpdateRequest request) {
        Explanation current = explanationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해설을 찾을 수 없습니다."));

        // 세터가 없다면 'merge 패턴'으로 빌더 재생성 후 save (JPA가 merge)
        Explanation updated = Explanation.builder()
                .id(current.getId())
                .problem(current.getProblem())
                .choiceNo(request.getChoiceNo() != null ? request.getChoiceNo() : current.getChoiceNo())
                .content(request.getContent())
                .build();

        explanationRepository.save(updated);
    }

    @Override
    public void delete(Long id) {
        explanationRepository.deleteById(id);
    }
}
