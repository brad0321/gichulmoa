package com.pro.project01.v2.domain.unit.entity;

import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "unit")
public class Unit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 단원명 예: 부동산의 개념과 분류 */
    @Column(nullable = false)
    private String name;

    /** 단원 순번 코드(UUU: 3자리, 예: 001, 003) */
    @Column(name = "seq_code", length = 3, nullable = false)
    private String seqCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    /** (요구사항대로) 단원도 회차에 속하도록 매핑 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;
}
