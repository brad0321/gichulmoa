package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;

import java.util.List;

public interface ProblemService {
    List<ProblemResponse> findAll();
    ProblemResponse findById(Long id);
    Long create(ProblemRequest request, String imagePath); // ✅ 수정
    void update(Long id, ProblemRequest request, String imagePath); // ✅ 수정
    void delete(Long id);
}
