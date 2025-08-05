package com.pro.project01.v2.domain.round.entity;

import com.pro.project01.v2.domain.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer roundNumber; // 예: 36, 35 (회차)

    @Column(name = "name")
    private String name; // 36회차, 35회차 등 표시용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}
