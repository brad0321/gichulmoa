package com.pro.project01.v2.domain.problemkeyword.service;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.problemkeyword.dto.ProblemKeywordCreateRequest;
import com.pro.project01.v2.domain.problemkeyword.dto.ProblemKeywordResponse;
import com.pro.project01.v2.domain.problemkeyword.entity.ProblemKeyword;
import com.pro.project01.v2.domain.problemkeyword.entity.ProblemKeyword.ProblemKeywordId;
import com.pro.project01.v2.domain.problemkeyword.repository.ProblemKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemKeywordServiceImpl implements ProblemKeywordService {

    private final ProblemKeywordRepository keywordRepository;
    private final ProblemRepository problemRepository;

    @Override
    public void create(ProblemKeywordCreateRequest request) {
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 문제 없음"));

        ProblemKeyword keyword = ProblemKeyword.builder()
                .id(new ProblemKeywordId(request.problemId(), request.keyword()))
                .problem(problem)
                .build();

        keywordRepository.save(keyword);
    }

    @Override
    public List<ProblemKeywordResponse> findByProblemId(Long problemId) {
        return keywordRepository.findByProblemId(problemId).stream()
                .map(k -> new ProblemKeywordResponse(
                        k.getId().getProblemId(),
                        k.getId().getKeyword()
                ))
                .toList();
    }

    @Override
    public void deleteByProblemId(Long problemId) {
        keywordRepository.deleteByProblemId(problemId);
    }
}
