// src/main/java/com/pro/project01/v2/domain/practice/dto/PracticeStartResponse.java
package com.pro.project01.v2.domain.practice.dto;

import java.util.List;

public record PracticeStartResponse(
        String sessionId,
        List<ProblemForSolveDto> firstPage
) {}
