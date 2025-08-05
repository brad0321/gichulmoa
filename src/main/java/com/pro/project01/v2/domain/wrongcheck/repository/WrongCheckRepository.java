package com.pro.project01.v2.domain.wrongcheck.repository;

import com.pro.project01.v2.domain.wrongcheck.entity.WrongCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WrongCheckRepository extends JpaRepository<WrongCheck, Long> {

    List<WrongCheck> findByUserId(Long userId);

    Optional<WrongCheck> findByUserIdAndProblemId(Long userId, Long problemId);

    boolean existsByUserIdAndProblemId(Long userId, Long problemId);
}
