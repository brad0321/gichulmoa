package com.pro.project01.v2.domain.explanation.repository;

import com.pro.project01.v2.domain.explanation.entity.Explanation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExplanationRepository extends JpaRepository<Explanation, Long> {

    List<Explanation> findByProblemId(Long problemId);
}
