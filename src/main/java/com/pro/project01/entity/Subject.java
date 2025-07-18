package com.pro.project01.entity;

import jakarta.persistence.*;
import lombok.*;

// Subject.java
@Entity
@Table(name = "Subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
