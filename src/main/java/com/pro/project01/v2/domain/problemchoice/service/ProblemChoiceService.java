package com.pro.project01.v2.domain.problemchoice.service;

import com.pro.project01.v2.domain.problemchoice.dto.*;

import java.util.List;

public interface ProblemChoiceService {

    Long create(ProblemChoiceCreateRequest request);

    ProblemChoiceResponse findById(Long id);

    List<ProblemChoiceResponse> findByProblemId(Long problemId);

    void update(Long id, ProblemChoiceUpdateRequest request);

    void delete(Long id);

    void deleteByProblemId(Long problemId);
}
