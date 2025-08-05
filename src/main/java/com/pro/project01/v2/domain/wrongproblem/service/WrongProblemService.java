package com.pro.project01.v2.domain.wrongproblem.service;

import com.pro.project01.v2.domain.wrongproblem.dto.WrongProblemCreateRequest;
import com.pro.project01.v2.domain.wrongproblem.dto.WrongProblemResponse;
import com.pro.project01.v2.domain.wrongproblem.dto.WrongProblemUpdateRequest;

import java.util.List;

public interface WrongProblemService {

    Long create(WrongProblemCreateRequest request);

    void update(Long id, WrongProblemUpdateRequest request);

    void delete(Long id);

    WrongProblemResponse findById(Long id);

    List<WrongProblemResponse> findByUserId(Long userId);
}
