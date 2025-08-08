package com.pro.project01.v2.domain.wrongnote.service;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import com.pro.project01.v2.domain.wrongnote.dto.WrongNoteCreateRequest;
import com.pro.project01.v2.domain.wrongnote.dto.WrongNoteResponse;
import com.pro.project01.v2.domain.wrongnote.dto.WrongNoteUpdateRequest;
import com.pro.project01.v2.domain.wrongnote.entity.WrongNote;
import com.pro.project01.v2.domain.wrongnote.repository.WrongNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WrongNoteServiceImpl implements WrongNoteService {

    private final WrongNoteRepository wrongNoteRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    @Override
    @Transactional
    public Long create(WrongNoteCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("문제 없음"));

        WrongNote wrongNote = WrongNote.builder()
                .user(user)
                .problem(problem)
                .memo(request.memo())
                .build();

        return wrongNoteRepository.save(wrongNote).getId();
    }

    @Override
    public WrongNoteResponse findById(Long id) {
        WrongNote wn = wrongNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("오답노트 없음"));
        return WrongNoteResponse.from(wn);
    }

    @Override
    public List<WrongNoteResponse> findByUserId(Long userId) {
        return wrongNoteRepository.findByUserId(userId)
                .stream()
                .map(WrongNoteResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void update(Long id, WrongNoteUpdateRequest request) {
        WrongNote wn = wrongNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("오답노트 없음"));
        wn.updateMemo(request.memo());
    }

    @Override
    public void delete(Long id) {
        wrongNoteRepository.deleteById(id);
    }
}
