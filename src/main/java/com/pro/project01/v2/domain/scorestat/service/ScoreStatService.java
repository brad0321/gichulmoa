package com.pro.project01.v2.domain.scorestat.service;

import com.pro.project01.v2.domain.scorestat.dto.ScoreStatCreateRequest;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatResponse;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatUpdateRequest;

import java.util.List;

public interface ScoreStatService {
    // 대시보드/관리용 조회
    List<ScoreStatResponse> findAll();
    List<ScoreStatResponse> findByUserId(Long userId);
    ScoreStatResponse findById(Long id);

    // 생성/수정/삭제
    void create(ScoreStatCreateRequest request);
    void update(Long id, ScoreStatUpdateRequest request);
    void delete(Long id);

    // 즉답 기록(문제 풀이 시 매 제출마다 호출)
    void record(Long userId, Long subjectId, Long roundId, boolean correct);
}
