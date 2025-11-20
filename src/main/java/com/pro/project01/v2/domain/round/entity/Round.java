package com.pro.project01.v2.domain.round.entity;

import com.pro.project01.v2.domain.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "round")
public class Round {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 회차 번호(RR: 2~3자리, 예: 35, 36) */
    @Column(name = "Round_number", nullable = false)
    private Short roundNumber;

    /** 36회차, 35회차 등 표시용 */
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}
