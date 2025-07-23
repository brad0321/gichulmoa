package com.pro.project01.v2.domain.memo.service;

import com.pro.project01.v2.domain.memo.dto.MemoCreateRequest;
import com.pro.project01.v2.domain.memo.dto.MemoResponse;
import com.pro.project01.v2.domain.memo.dto.MemoUpdateRequest;
import com.pro.project01.v2.domain.memo.entity.Memo;
import com.pro.project01.v2.domain.memo.repository.MemoRepository;
import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MemoRepository memoRepository;

    @Override
    public Long create(MemoCreateRequest request) {
        Memo memo = Memo.builder()
                .user(User.builder().id(request.userId()).build())
                .problem(Problem.builder().id(request.problemId()).build())
                .content(request.content())
                .build();
        return memoRepository.save(memo).getId();
    }

    @Override
    public MemoResponse findById(Long id) {
        return toResponse(
                memoRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("메모를 찾을 수 없습니다."))
        );
    }

    @Override
    public List<MemoResponse> findByUserId(Long userId) {
        return memoRepository.findByUserId(userId)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MemoResponse findByUserIdAndProblemId(Long userId, Long problemId) {
        return toResponse(
                memoRepository.findByUserIdAndProblemId(userId, problemId)
                        .orElseThrow(() -> new EntityNotFoundException("해당 문제에 대한 메모가 없습니다."))
        );
    }

    @Override
    public void update(Long id, MemoUpdateRequest request) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("메모를 찾을 수 없습니다."));
        memo.updateContent(request.content());
    }

    @Override
    public void delete(Long id) {
        memoRepository.deleteById(id);
    }

    private MemoResponse toResponse(Memo memo) {
        return new MemoResponse(
                memo.getId(),
                memo.getUser().getId(),
                memo.getProblem().getId(),
                memo.getContent(),
                memo.getCreatedAt(),
                memo.getUpdatedAt()
        );
    }
}
