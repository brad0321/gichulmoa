package com.pro.project01.v2.domain.problem.repository;

import com.pro.project01.v2.domain.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long>
{
}
