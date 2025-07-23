package com.pro.project01.v2.domain.explanation.dto;

import java.time.LocalDateTime;

public record ExplanationResponse(
        Long id,
        Long problemId,
        Integer choiceNo,
        String content
) {}
