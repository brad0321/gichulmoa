package com.pro.project01.v2.domain.problemchoice.repository;

import com.pro.project01.v2.domain.problemchoice.entity.ProblemChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemChoiceRepository extends JpaRepository<ProblemChoice, Long> {

    List<ProblemChoice> findByProblemId(Long problemId);

    void deleteByProblemId(Long problemId);
}

