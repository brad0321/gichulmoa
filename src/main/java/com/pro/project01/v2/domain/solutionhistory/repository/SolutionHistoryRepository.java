package com.pro.project01.v2.domain.solutionhistory.repository;

import com.pro.project01.v2.domain.solutionhistory.entity.SolutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolutionHistoryRepository extends JpaRepository<SolutionHistory, Long> {

    List<SolutionHistory> findByUserId(Long userId);

    List<SolutionHistory> findByProblemId(Long problemId);

    Optional<SolutionHistory> findByUserIdAndProblemId(Long userId, Long problemId);

    boolean existsByUserIdAndProblemId(Long userId, Long problemId);
}
