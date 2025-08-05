package com.pro.project01.v2.domain.wrongcheck.service;

import com.pro.project01.v2.domain.wrongcheck.dto.WrongCheckCreateRequest;
import com.pro.project01.v2.domain.wrongcheck.dto.WrongCheckUpdateRequest;

import java.util.List;
import com.pro.project01.v2.domain.wrongcheck.dto.WrongCheckResponse;

public interface WrongCheckService {

    Long create(WrongCheckCreateRequest request);

    void update(Long id, WrongCheckUpdateRequest request);

    void delete(Long id);

    WrongCheckResponse findById(Long id);

    List<WrongCheckResponse> findByUserId(Long userId);
}
