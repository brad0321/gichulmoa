package com.pro.project01.v2.domain.subject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "subject")
public class Subject {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 과목 코드(SS: 2자리, 예: 01) */
    @Column(name = "code", length = 2, nullable = false, unique = true)
    private String code;

    /** 과목명 예: 부동산학개론, 민법 */
    @Column(nullable = false, unique = true)
    private String name;
}
