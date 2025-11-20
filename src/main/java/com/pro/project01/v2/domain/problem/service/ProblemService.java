package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.*;
import com.pro.project01.v2.domain.problem.dto.ProblemCodeDtos.CodesResponse;
import com.pro.project01.v2.domain.problem.dto.ProblemCodeDtos.UpdateCodesRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ProblemService {

    /* ====================== 기본 조회 ====================== */
    List<ProblemResponse> findAll();
    ProblemResponse findById(Long id);

    /* ====================== 생성 ====================== */
    Long create(ProblemRequest request, String imagePath);
    Long create(ProblemRequest request, String imagePath, List<String> exps); // choice별 해설 포함

    /* ====================== 수정 ====================== */
    void update(Long id, ProblemRequest request, String imagePath);
    void update(Long id, ProblemRequest request, String imagePath, List<String> exps); // choice별 해설 포함

    /* ====================== 삭제 ====================== */
    void delete(Long id);

    /* ====================== 검색/필터링 ====================== */
    List<ProblemResponse> findByFilters(Long subjectId, List<Long> roundIds, List<Long> unitIds);

    /* ====================== 순서 이동 ====================== */
    void moveUp(Long id);
    void moveDown(Long id);

    /* ====================== 카운트 ====================== */
    long countByFilters(Long subjectId, List<Long> roundIds, List<Long> unitIds);

    /* ====================== ✅ 커서 기반 무한스크롤 목록 ====================== */
    /**
     * 무한스크롤 문제 목록 조회 (커서 기반, 복수 회차/단원 필터)
     *
     * @param subjectId 과목 ID (필터, null 이면 전체)
     * @param roundIds  회차 ID 목록 (필터, null 또는 빈 리스트면 전체)
     * @param unitIds   단원 ID 목록 (필터, null 또는 빈 리스트면 전체)
     * @param q         검색어 (문제 제목)
     * @param cursorId  마지막으로 조회한 문제의 ID (커서)
     * @param size      페이지 크기
     * @return Slice<ProblemListItemView>
     */
    Slice<ProblemListItemView> getProblemList(
            Long subjectId,
            List<Long> roundIds,
            List<Long> unitIds,
            String q,
            Long cursorId,
            int size
    );

    /* ====================== 코드 관련 ====================== */
    CodesResponse getCodes(Long problemId);
    void updateCodes(Long problemId, UpdateCodesRequest req);
}
