package com.pro.project01.v2.domain.practice.entity;

import com.pro.project01.v2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="practice_session",
        indexes = { @Index(columnList="user_id,status"), @Index(columnList="lastAccessedAt") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PracticeSession {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private SessionStatus status;

    @Column(nullable=false)
    private Integer currentIndex;

    private LocalDateTime lastAccessedAt;

    private Long subjectId;

    @ElementCollection
    @CollectionTable(name="practice_session_rounds", joinColumns=@JoinColumn(name="session_id"))
    @Column(name="round_id")
    @Builder.Default
    private List<Long> roundIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="practice_session_units", joinColumns=@JoinColumn(name="session_id"))
    @Column(name="unit_id")
    @Builder.Default
    private List<Long> unitIds = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.currentIndex == null) this.currentIndex = 0;
        if (this.status == null) this.status = SessionStatus.ACTIVE;
        this.lastAccessedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void onUpdate(){ this.updatedAt = LocalDateTime.now(); }
}
