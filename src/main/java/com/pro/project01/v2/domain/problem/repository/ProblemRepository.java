package com.pro.project01.v2.domain.problem.repository;

import com.pro.project01.v2.domain.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long>
{
    @Query("SELECT p FROM Problem p WHERE p.subject.id = :subjectId " +
            "AND (:roundId IS NULL OR p.round.id = :roundId) " +
            "AND (:unitId IS NULL OR p.unit.id = :unitId)")
    List<Problem> findByFilters(@Param("subjectId") Long subjectId,
                                @Param("roundId") Long roundId,
                                @Param("unitId") Long unitId);
}
