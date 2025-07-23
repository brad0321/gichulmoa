package com.pro.project01.v2.domain.problem.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProblemCreateRequest {

    private String title;                // 문제 제목
    private String content;              // 지문 내용 (optional)
    private MultipartFile imageFile;     // 이미지 파일 (optional)

    private Long subjectId;              // 과목 (Subject FK)
    private Long roundId;                // 회차 (Round FK)
    private Long unitId;                 // 목차 (Unit FK)

    private String viewContent;          // 보기 지문 텍스트

    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String choice5;
}
