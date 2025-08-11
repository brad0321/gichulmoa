package com.pro.project01.v2.domain.explanation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExplanationCreateRequest {
    private Long problemId;
    private String content;
    private Integer choiceNo;
}
