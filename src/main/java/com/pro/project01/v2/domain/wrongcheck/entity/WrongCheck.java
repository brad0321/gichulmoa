package com.pro.project01.v2.domain.wrongcheck.entity;

import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wrong_checks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor               // Lombok이 전체 필드 생성자 1개 자동 생성
@Builder                          // Builder 패턴 사용
public class WrongCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 문제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    // 정답 여부
    @Column(nullable = false)
    private boolean correct;

    // 선택적 태그
    @Column(length = 255)
    private String tag;

    // 생성 시간
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateTag(String tag) {
        this.tag = tag;
    }
}

