package com.pro.project01.v2.domain.problem.repository;

import com.pro.project01.v2.domain.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long>
{
    @Query("SELECT p FROM Problem p WHERE p.subject.id = :subjectId " +
            "AND (:roundIds IS NULL OR p.round.id IN :roundIds) " +
            "AND (:unitIds IS NULL OR p.unit.id IN :unitIds)")
    List<Problem> findByFilters(@Param("subjectId") Long subjectId,
                                @Param("roundIds") List<Long> roundIds,
                                @Param("unitIds") List<Long> unitIds);
}
