package com.pro.project01.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // ✅ 수정: TEXT로 처리 (HTML 가능)
    @Lob
    @Column(name = "view_content", columnDefinition = "TEXT")
    private String viewContent;

    private String viewImagePath;

    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String choice5;
}
