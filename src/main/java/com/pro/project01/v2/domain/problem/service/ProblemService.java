package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.ProblemCreateRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.dto.ProblemUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProblemService {

    Long create(ProblemCreateRequest request);

    List<ProblemResponse> findAll();

    ProblemResponse findById(Long id);

    void update(Long id, ProblemUpdateRequest request, MultipartFile viewImage); // ✅ 이미지 추가

    void delete(Long id);
}
