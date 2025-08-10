package com.pro.project01.v2.domain.problem.dto;

import jakarta.validation.constraints.NotNull;

public record SolveSubmitRequest(
        @NotNull Long problemId,
        @NotNull Integer selected
) {}
