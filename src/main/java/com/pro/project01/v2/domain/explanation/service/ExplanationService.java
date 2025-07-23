package com.pro.project01.v2.domain.explanation.service;

import com.pro.project01.v2.domain.explanation.dto.ExplanationCreateRequest;
import com.pro.project01.v2.domain.explanation.dto.ExplanationResponse;
import com.pro.project01.v2.domain.explanation.dto.ExplanationUpdateRequest;

import java.util.List;

public interface ExplanationService {

    Long create(ExplanationCreateRequest request);

    ExplanationResponse findById(Long id);

    List<ExplanationResponse> findByProblemId(Long problemId);

    void update(Long id, ExplanationUpdateRequest request);

    void delete(Long id);
}
