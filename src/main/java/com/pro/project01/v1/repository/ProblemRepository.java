package com.pro.project01.v1.repository;

import com.pro.project01.v1.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
