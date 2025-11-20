// src/main/java/com/pro/project01/v2/domain/problem/repository/ProblemRepository.java
package com.pro.project01.v2.domain.problem.repository;

import com.pro.project01.v2.domain.problem.dto.ProblemListItemView;
import com.pro.project01.v2.domain.problem.entity.Problem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    /* ==========================
       🔹 기본 유틸 (정렬, 이전/다음)
       ========================== */
    @Query("""
        SELECT p FROM Problem p
        ORDER BY CASE WHEN p.subject.name = '부동산학개론' THEN 1
                      WHEN p.subject.name = '민법'       THEN 2
                      ELSE 3 END,
                 p.round.roundNumber DESC
    """)
    List<Problem> findAllByCustomOrder();

    @Query("""
        SELECT p FROM Problem p
        ORDER BY CASE p.subject.code
                   WHEN '01' THEN 1
                   WHEN '02' THEN 2
                   ELSE 3
                 END,
                 p.round.roundNumber DESC
    """)
    List<Problem> findAllByCustomOrderBySubjectCode();

    @Query("SELECT MAX(p.orderNo) FROM Problem p")
    Integer findMaxOrderNo();

    @Query("SELECT p FROM Problem p WHERE p.orderNo < :orderNo ORDER BY p.orderNo DESC")
    List<Problem> findPrevByOrderNo(@Param("orderNo") int orderNo, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE p.orderNo > :orderNo ORDER BY p.orderNo ASC")
    List<Problem> findNextByOrderNo(@Param("orderNo") int orderNo, Pageable pageable);

    /* ==========================
       🔹 필터 기반 조회/카운트
       ========================== */
    @Query("""
        SELECT p FROM Problem p
        WHERE (:subjectId IS NULL OR p.subject.id = :subjectId)
          AND (:roundIds IS NULL OR p.round.id IN :roundIds)
          AND (:unitIds IS NULL OR p.unit.id IN :unitIds)
        ORDER BY p.orderNo ASC
    """)
    List<Problem> findByFilters(
            @Param("subjectId") Long subjectId,
            @Param("roundIds")  List<Long> roundIds,
            @Param("unitIds")   List<Long> unitIds
    );

    @Query("""
        SELECT COUNT(p) FROM Problem p
        WHERE (:subjectId IS NULL OR p.subject.id = :subjectId)
          AND (:roundIds IS NULL OR p.round.id IN :roundIds)
          AND (:unitIds IS NULL OR p.unit.id IN :unitIds)
    """)
    long countByFilters(
            @Param("subjectId") Long subjectId,
            @Param("roundIds")  List<Long> roundIds,
            @Param("unitIds")   List<Long> unitIds
    );

    /* =========================================================
       ✅ 커서 기반 무한스크롤 목록 조회 (Keyset Paging)
       ---------------------------------------------------------
       - 현재 커서는 p.id 기준 유지
       - 정렬도 p.id ASC 유지 (기존 로직 안 깨지게)
       - 단, Projection 에 subjectProblemNo 추가
       ========================================================= */
    @Query("""
        SELECT
            p.id               AS id,
            p.title            AS title,
            s.name             AS subjectName,
            r.name             AS roundName,
            r.roundNumber      AS roundNumber,
            u.name             AS unitName,
            p.roundProblemNo   AS roundProblemNo,
            p.subjectProblemNo AS subjectProblemNo
        FROM Problem p
            LEFT JOIN p.subject s
            LEFT JOIN p.round   r
            LEFT JOIN p.unit    u
        WHERE (:subjectId IS NULL OR p.subject.id = :subjectId)
          AND (:roundIds IS NULL OR p.round.id IN :roundIds)
          AND (:unitIds  IS NULL OR p.unit.id  IN :unitIds)
          AND (:q IS NULL OR p.title LIKE CONCAT('%', :q, '%'))
          AND (:cursorId IS NULL OR p.id > :cursorId)
        ORDER BY p.id ASC
        """)
    List<ProblemListItemView> findNextPageForInfiniteScroll(
            @Param("subjectId") Long subjectId,
            @Param("roundIds") List<Long> roundIds,
            @Param("unitIds") List<Long> unitIds,
            @Param("q") String q,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    /* =========================================================
       🔁 id DESC 버전 — 최신순 정렬 시 사용
       ========================================================= */
    @Query("""
        SELECT
            p.id               AS id,
            p.title            AS title,
            s.name             AS subjectName,
            r.name             AS roundName,
            r.roundNumber      AS roundNumber,
            u.name             AS unitName,
            p.roundProblemNo   AS roundProblemNo,
            p.subjectProblemNo AS subjectProblemNo,
            p.createdAt        AS createdAt
        FROM Problem p
            LEFT JOIN p.subject s
            LEFT JOIN p.round   r
            LEFT JOIN p.unit    u
        WHERE (:subjectId IS NULL OR p.subject.id = :subjectId)
          AND (:roundIds IS NULL OR p.round.id IN :roundIds)
          AND (:unitIds  IS NULL OR p.unit.id  IN :unitIds)
          AND (:q IS NULL OR p.title LIKE CONCAT('%', :q, '%'))
          AND (:cursorId IS NULL OR p.id < :cursorId)
        ORDER BY p.id DESC
    """)
    List<ProblemListItemView> findPrevPageForInfiniteScroll(
            @Param("subjectId") Long subjectId,
            @Param("roundIds")  List<Long> roundIds,
            @Param("unitIds")   List<Long> unitIds,
            @Param("q")         String q,
            @Param("cursorId")  Long cursorId,
            Pageable pageable
    );
}
