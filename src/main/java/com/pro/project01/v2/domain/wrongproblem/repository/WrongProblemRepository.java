package com.pro.project01.v2.domain.wrongproblem.repository;

import com.pro.project01.v2.domain.wrongproblem.entity.WrongProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WrongProblemRepository extends JpaRepository<WrongProblem, Long> {

    List<WrongProblem> findByUserId(Long userId);

    boolean existsByUserIdAndProblemId(Long userId, Long problemId);
}
