package com.pro.project01.v2.domain.scorestat.service;

import com.pro.project01.v2.domain.scorestat.dto.ScoreStatCreateRequest;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatResponse;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatUpdateRequest;

import java.util.List;

public interface ScoreStatService {

    Long create(ScoreStatCreateRequest request);

    void update(Long id, ScoreStatUpdateRequest request);

    ScoreStatResponse findById(Long id);

    List<ScoreStatResponse> findByUserId(Long userId);

    List<ScoreStatResponse> findAll();

    void delete(Long id);
}
