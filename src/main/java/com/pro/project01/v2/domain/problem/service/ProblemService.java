package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;

import java.util.List;

public interface ProblemService {
    List<ProblemResponse> findAll();
    ProblemResponse findById(Long id);
    Long create(ProblemRequest request, String imagePath);
    void update(Long id, ProblemRequest request, String imagePath);
    void delete(Long id);

    // 컨트롤러에서 호출하는 확장 메서드
    Long create(ProblemRequest request, String imagePath, List<String> exps);
    void update(Long id, ProblemRequest request, String imagePath, List<String> exps);
}

