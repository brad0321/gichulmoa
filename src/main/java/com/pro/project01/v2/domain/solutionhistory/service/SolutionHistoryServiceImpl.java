package com.pro.project01.v2.domain.solutionhistory.service;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryCreateRequest;
import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryResponse;
import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryUpdateRequest;
import com.pro.project01.v2.domain.solutionhistory.entity.SolutionHistory;
import com.pro.project01.v2.domain.solutionhistory.repository.SolutionHistoryRepository;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class SolutionHistoryServiceImpl implements SolutionHistoryService {

    private final SolutionHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    @Override
    public Long create(SolutionHistoryCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("문제 없음"));

        SolutionHistory history = SolutionHistory.builder()
                .user(user)
                .problem(problem)
                .isCorrect(request.isCorrect())
                .build();

        return historyRepository.save(history).getId();
    }

    @Override
    public SolutionHistoryResponse findById(Long id) {
        SolutionHistory h = historyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("풀이 이력 없음"));
        return toResponse(h);
    }

    @Override
    public List<SolutionHistoryResponse> findByUser(Long userId) {
        return historyRepository.findByUserId(userId);
    }

    @Override
    public List<SolutionHistoryResponse> findByProblem(Long problemId) {
        return historyRepository.findByProblemId(problemId);
    }

    @Override
    public void updateCorrect(Long id, SolutionHistoryUpdateRequest request) {
        SolutionHistory h = historyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("풀이 이력 없음"));
        h.setCorrect(request.isCorrect());
    }

    @Override
    public void delete(Long id) {
        historyRepository.deleteById(id);
    }

    private SolutionHistoryResponse toResponse(SolutionHistory h) {
        return new SolutionHistoryResponse(
                h.getId(),
                h.getUser().getId(),
                h.getProblem().getId(),
                h.getIsCorrect(),
                h.getSolvedAt()
        );
    }
}
