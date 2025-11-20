// src/main/java/com/pro/project01/v2/domain/problem/dto/ProblemListSliceResponse3Cursor.java
package com.pro.project01.v2.domain.problem.dto;

import java.util.List;

public record ProblemListSliceResponse3Cursor(
        List<ProblemListItemDto> items,
        boolean hasNext,
        Integer nextCursorRoundNumber,
        Integer nextCursorRoundProblemNo,
        Long nextCursorId
) {}
