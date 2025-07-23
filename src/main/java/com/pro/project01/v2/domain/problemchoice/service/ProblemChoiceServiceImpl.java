package com.pro.project01.v2.domain.problemchoice.service;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.problemchoice.dto.*;
import com.pro.project01.v2.domain.problemchoice.entity.ProblemChoice;
import com.pro.project01.v2.domain.problemchoice.repository.ProblemChoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemChoiceServiceImpl implements ProblemChoiceService {

    private final ProblemChoiceRepository problemChoiceRepository;
    private final ProblemRepository problemRepository;

    @Override
    public Long create(ProblemChoiceCreateRequest request) {
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));

        ProblemChoice choice = ProblemChoice.builder()
                .problem(problem)
                .content(request.content())
                .isAnswer(request.isAnswer())
                .build();

        return problemChoiceRepository.save(choice).getId();
    }

    @Override
    public ProblemChoiceResponse findById(Long id) {
        return problemChoiceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("선택지를 찾을 수 없습니다."));
    }

    @Override
    public List<ProblemChoiceResponse> findByProblemId(Long problemId) {
        return problemChoiceRepository.findByProblemId(problemId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Long id, ProblemChoiceUpdateRequest request) {
        ProblemChoice choice = problemChoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("선택지를 찾을 수 없습니다."));
        choice.updateContent(request.content());
        choice.markAsAnswer(request.isAnswer());
    }

    @Override
    public void delete(Long id) {
        problemChoiceRepository.deleteById(id);
    }

    @Override
    public void deleteByProblemId(Long problemId) {
        problemChoiceRepository.deleteByProblemId(problemId);
    }

    private ProblemChoiceResponse toResponse(ProblemChoice entity) {
        return new ProblemChoiceResponse(
                entity.getId(),
                entity.getProblem().getId(),
                entity.getContent(),
                entity.isAnswer()
        );
    }
}
