package com.pro.project01.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String viewContent;  // 보기 항목 텍스트 (HTML)

    private String viewImagePath; // 업로드 이미지 파일 경로

    @Column(name = "choice_1")
    private String choice1;

    @Column(name = "choice_2")
    private String choice2;

    @Column(name = "choice_3")
    private String choice3;

    @Column(name = "choice_4")
    private String choice4;

    @Column(name = "choice_5")
    private String choice5;
}
