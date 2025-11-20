// src/main/java/com/pro/project01/v2/domain/practice/dto/PracticeStartRequest.java
package com.pro.project01.v2.domain.practice.dto;

import java.util.List;

public record PracticeStartRequest(
        Long subjectId,
        List<Long> roundIds,
        List<Long> unitIds
) {}
