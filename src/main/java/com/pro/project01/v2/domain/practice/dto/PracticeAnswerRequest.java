// src/main/java/com/pro/project01/v2/domain/practice/dto/PracticeAnswerRequest.java
package com.pro.project01.v2.domain.practice.dto;

public record PracticeAnswerRequest(
        String sessionId,
        Long itemId,
        int selected
) {}
