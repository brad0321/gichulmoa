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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExplanationServiceImpl implements ExplanationService {

    private final ExplanationRepository explanationRepository;
    private final ProblemRepository problemRepository;

    @Override
    public Long create(ExplanationCreateRequest request) {
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 문제를 찾을 수 없습니다."));

        Explanation explanation = Explanation.builder()
                .choiceNo(request.choiceNo())
                .content(request.content())
                .problem(problem)
                .build();

        return explanationRepository.save(explanation).getId();
    }

    @Override
    public ExplanationResponse findById(Long id) {
        Explanation explanation = explanationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해설을 찾을 수 없습니다."));

        return toResponse(explanation);
    }

    @Override
    public List<ExplanationResponse> findByProblemId(Long problemId) {
        return explanationRepository.findByProblemId(problemId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void update(Long id, ExplanationUpdateRequest request) {
        Explanation explanation = explanationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해설을 찾을 수 없습니다."));

        explanation = Explanation.builder()
                .id(explanation.getId())
                .problem(explanation.getProblem())
                .choiceNo(request.choiceNo())
                .content(request.content())
                .build();

        explanationRepository.save(explanation);
    }

    @Override
    public void delete(Long id) {
        explanationRepository.deleteById(id);
    }

    private ExplanationResponse toResponse(Explanation e) {
        return new ExplanationResponse(
                e.getId(),
                e.getProblem().getId(),
                e.getChoiceNo(),
                e.getContent()
        );
    }
}
