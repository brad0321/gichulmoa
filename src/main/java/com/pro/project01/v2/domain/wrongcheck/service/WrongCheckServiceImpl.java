package com.pro.project01.v2.domain.wrongcheck.service;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import com.pro.project01.v2.domain.wrongcheck.dto.WrongCheckCreateRequest;
import com.pro.project01.v2.domain.wrongcheck.dto.WrongCheckResponse;
import com.pro.project01.v2.domain.wrongcheck.dto.WrongCheckUpdateRequest;
import com.pro.project01.v2.domain.wrongcheck.entity.WrongCheck;
import com.pro.project01.v2.domain.wrongcheck.repository.WrongCheckRepository;
import com.pro.project01.v2.domain.wrongnote.entity.WrongNote;
import com.pro.project01.v2.domain.wrongnote.repository.WrongNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WrongCheckServiceImpl implements WrongCheckService {

    private final WrongCheckRepository wrongCheckRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final WrongNoteRepository wrongNoteRepository;

    @Override
    @Transactional
    public Long create(WrongCheckCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("문제 없음"));

        boolean isCorrect = problem.getAnswer().equals(request.answer());

        // ✅ WrongCheck 저장
        WrongCheck wrongCheck = WrongCheck.builder()
                .user(user)
                .problem(problem)
                .correct(isCorrect)
                .tag(request.tag())
                .build();
        wrongCheckRepository.save(wrongCheck);


        // ✅ 오답이면 WrongNote 자동 생성
        if (!isCorrect && !wrongNoteRepository.existsByUserIdAndProblemId(user.getId(), problem.getId())) {
            WrongNote wn = WrongNote.builder()
                    .user(user)
                    .problem(problem)
                    .build();
            wrongNoteRepository.save(wn);
        }

        return wrongCheck.getId();
    }

    @Override
    public void update(Long id, WrongCheckUpdateRequest request) {
        WrongCheck entity = wrongCheckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WrongCheck 없음"));
        entity.updateTag(request.tag());
    }

    @Override
    public void delete(Long id) {
        wrongCheckRepository.deleteById(id);
    }

    @Override
    public WrongCheckResponse findById(Long id) {
        WrongCheck entity = wrongCheckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WrongCheck 없음"));
        return toResponse(entity);
    }

    @Override
    public List<WrongCheckResponse> findByUserId(Long userId) {
        return wrongCheckRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private WrongCheckResponse toResponse(WrongCheck entity) {
        return new WrongCheckResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getProblem().getId(),
                entity.isCorrect(),
                entity.getTag(),
                entity.getCreatedAt()
        );
    }
}
