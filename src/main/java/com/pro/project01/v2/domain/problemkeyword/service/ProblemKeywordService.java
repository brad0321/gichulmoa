package com.pro.project01.v2.domain.problemkeyword.service;

import com.pro.project01.v2.domain.problemkeyword.dto.ProblemKeywordCreateRequest;
import com.pro.project01.v2.domain.problemkeyword.dto.ProblemKeywordResponse;

import java.util.List;

public interface ProblemKeywordService {

    void create(ProblemKeywordCreateRequest request);

    List<ProblemKeywordResponse> findByProblemId(Long problemId);

    void deleteByProblemId(Long problemId);
}
