// src/main/java/com/pro/project01/v2/domain/practice/dto/PracticeAnswerResponse.java
package com.pro.project01.v2.domain.practice.dto;

public record PracticeAnswerResponse(
        boolean correct,
        int answer,
        String explanation
) {}
