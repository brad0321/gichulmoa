package com.pro.project01.v2.domain.problem.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProblemUpdateRequest {

    private String title;
    private String content;
    private MultipartFile imageFile;

    private Long subjectId;
    private Long roundId;
    private Long unitId;

    private String viewContent;

    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String choice5;
}
