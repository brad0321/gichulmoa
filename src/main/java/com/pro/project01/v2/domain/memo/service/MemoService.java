package com.pro.project01.v2.domain.memo.service;

import com.pro.project01.v2.domain.memo.dto.MemoCreateRequest;
import com.pro.project01.v2.domain.memo.dto.MemoResponse;
import com.pro.project01.v2.domain.memo.dto.MemoUpdateRequest;

import java.util.List;

public interface MemoService {

    Long create(MemoCreateRequest request);

    MemoResponse findById(Long id);

    List<MemoResponse> findByUserId(Long userId);

    MemoResponse findByUserIdAndProblemId(Long userId, Long problemId);

    void update(Long id, MemoUpdateRequest request);

    void delete(Long id);
}
