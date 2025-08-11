package com.pro.project01.v2.domain.solutionhistory.repository;

import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryResponse;
import com.pro.project01.v2.domain.solutionhistory.entity.SolutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolutionHistoryRepository extends JpaRepository<SolutionHistory, Long>
{
    List<SolutionHistoryResponse> findByUserId(Long userId);
    List<SolutionHistoryResponse> findByProblemId(Long problemId);
}
