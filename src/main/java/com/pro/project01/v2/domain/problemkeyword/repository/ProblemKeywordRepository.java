package com.pro.project01.v2.domain.problemkeyword.repository;

import com.pro.project01.v2.domain.problemkeyword.entity.ProblemKeyword;
import com.pro.project01.v2.domain.problemkeyword.entity.ProblemKeyword.ProblemKeywordId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemKeywordRepository extends JpaRepository<ProblemKeyword, ProblemKeywordId> {

    List<ProblemKeyword> findByProblemId(Long problemId);

    void deleteByProblemId(Long problemId);
}
