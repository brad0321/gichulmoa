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

    @Lob
    @Column(name = "view_content", columnDefinition = "TEXT")
    private String viewContent;

    private String viewImagePath;

    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String choice5;

    // Problem.java
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private Round round;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;
}
