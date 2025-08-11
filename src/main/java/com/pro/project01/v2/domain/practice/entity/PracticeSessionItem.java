package com.pro.project01.v2.domain.practice.entity;

import com.pro.project01.v2.domain.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="practice_session_item",
        indexes=@Index(columnList="session_id,orderIndex"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PracticeSessionItem {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="session_id", nullable=false)
    private PracticeSession session;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="problem_id", nullable=false)
    private Problem problem;

    @Column(nullable=false)
    private Integer orderIndex;

    private Integer selectedChoiceNo;
    private Boolean isCorrect;

    @Column(nullable=false)
    private Integer eliminatedMask;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.eliminatedMask == null) this.eliminatedMask = 0;
    }
    @PreUpdate
    public void onUpdate(){ this.updatedAt = LocalDateTime.now(); }
}
