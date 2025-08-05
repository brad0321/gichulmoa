package com.pro.project01.v2.domain.wrongcheck.dto;

public record WrongCheckCreateRequest(
        Long userId,
        Long problemId,
        Integer answer,
        String tag // 태그는 선택 입력
) {}
