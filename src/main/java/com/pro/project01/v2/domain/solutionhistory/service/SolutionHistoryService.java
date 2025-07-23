package com.pro.project01.v2.domain.solutionhistory.service;

import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryCreateRequest;
import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryResponse;
import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryUpdateRequest;

import java.util.List;

public interface SolutionHistoryService {

    Long create(SolutionHistoryCreateRequest request);

    SolutionHistoryResponse findById(Long id);

    List<SolutionHistoryResponse> findByUser(Long userId);

    List<SolutionHistoryResponse> findByProblem(Long problemId);

    void updateCorrect(Long id, SolutionHistoryUpdateRequest request);

    void delete(Long id);
}
