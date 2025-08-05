package com.pro.project01.v2.domain.wrongproblem.service;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import com.pro.project01.v2.domain.wrongproblem.dto.WrongProblemCreateRequest;
import com.pro.project01.v2.domain.wrongproblem.dto.WrongProblemResponse;
import com.pro.project01.v2.domain.wrongproblem.dto.WrongProblemUpdateRequest;
import com.pro.project01.v2.domain.wrongproblem.entity.WrongProblem;
import com.pro.project01.v2.domain.wrongproblem.repository.WrongProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WrongProblemServiceImpl implements WrongProblemService {

    private final WrongProblemRepository wrongProblemRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    @Override
    @Transactional
    public Long create(WrongProblemCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("문제 없음"));

        WrongProblem wrongProblem = WrongProblem.builder()
                .user(user)
                .problem(problem)
                .reason(request.reason())
                .build();

        return wrongProblemRepository.save(wrongProblem).getId();
    }

    @Override
    @Transactional
    public void update(Long id, WrongProblemUpdateRequest request) {
        WrongProblem wp = wrongProblemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("틀린문제 없음"));
        wp.updateReason(request.reason());
    }

    @Override
    public void delete(Long id) {
        wrongProblemRepository.deleteById(id);
    }

    @Override
    public WrongProblemResponse findById(Long id) {
        WrongProblem wp = wrongProblemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("틀린문제 없음"));
        return toResponse(wp);
    }

    @Override
    public List<WrongProblemResponse> findByUserId(Long userId) {
        return wrongProblemRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private WrongProblemResponse toResponse(WrongProblem wp) {
        return new WrongProblemResponse(
                wp.getId(),
                wp.getUser().getId(),
                wp.getProblem().getId(),
                wp.getReason(),
                wp.getCreatedAt(),
                wp.getUpdatedAt()
        );
    }
}
