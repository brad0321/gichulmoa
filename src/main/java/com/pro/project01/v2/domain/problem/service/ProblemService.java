package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;

import java.util.List;

public interface ProblemService {
    ProblemResponse create(ProblemRequest request, String imagePath);
    List<ProblemResponse> findAll();
    ProblemResponse findById(Long id);
    void delete(Long id);
}
