package com.pro.project01.v1.entity;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
public class ProblemForm {
    private String title;
    private String viewContent;
    private MultipartFile viewImage;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String choice5;

    private Long subjectId;
    private Long roundId;
    private Long unitId;
}
