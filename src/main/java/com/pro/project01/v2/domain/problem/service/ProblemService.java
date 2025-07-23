package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.ProblemCreateRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.dto.ProblemUpdateRequest;

import java.util.List;

public interface ProblemService {

    Long create(ProblemCreateRequest request);
    void update(Long id, ProblemUpdateRequest request);
    void delete(Long id);
    ProblemResponse findById(Long id);
    List<ProblemResponse> findAll();
}

