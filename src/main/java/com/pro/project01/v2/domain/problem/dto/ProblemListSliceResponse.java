// src/main/java/com/pro/project01/v2/domain/problem/dto/ProblemListSliceResponse.java
package com.pro.project01.v2.domain.problem.dto;

import java.util.List;

public record ProblemListSliceResponse(
        List<ProblemListItemDto> items,
        Long nextCursor,
        boolean hasNext
) {}
