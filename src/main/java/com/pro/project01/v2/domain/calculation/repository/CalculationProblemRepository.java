package com.pro.project01.v2.domain.calculation.repository;

import com.pro.project01.v2.domain.calculation.entity.CalculationProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalculationProblemRepository extends JpaRepository<CalculationProblem, Long> {

    // ✅ 특정 Problem에 연결된 CalculationProblem 조회
    List<CalculationProblem> findByProblemId(Long problemId);

    // ✅ Problem ID로 CalculationProblem 존재 여부 확인
    boolean existsByProblemId(Long problemId);
}
