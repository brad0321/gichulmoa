package com.pro.project01.entity;

import jakarta.persistence.*;
import lombok.*;

// Unit.java
@Entity
@Table(name = "Unit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
