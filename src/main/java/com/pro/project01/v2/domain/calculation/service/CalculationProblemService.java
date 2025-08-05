package com.pro.project01.v2.domain.calculation.service;

import com.pro.project01.v2.domain.calculation.dto.CalculationProblemResponse;

import java.util.List;

public interface CalculationProblemService {

    // ✅ 계산문제 단건 조회
    CalculationProblemResponse findById(Long id);

    // ✅ 모든 계산문제 조회
    List<CalculationProblemResponse> findAll();

    // ✅ 특정 Problem ID 기반 조회
    CalculationProblemResponse findByProblemId(Long problemId);

    // ✅ 삭제
    void delete(Long id);
}
