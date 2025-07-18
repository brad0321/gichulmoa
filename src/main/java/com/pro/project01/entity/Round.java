package com.pro.project01.entity;

import jakarta.persistence.*;
import lombok.*;

// Round.java
@Entity
@Table(name = "Round")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}

