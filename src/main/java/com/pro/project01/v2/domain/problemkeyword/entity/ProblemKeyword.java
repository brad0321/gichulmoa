package com.pro.project01.v2.domain.problemkeyword.entity;

import com.pro.project01.v2.domain.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProblemKeyword {

    @EmbeddedId
    private ProblemKeywordId id;

    @MapsId("problemId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ProblemKeywordId implements java.io.Serializable {
        private Long problemId;
        private String keyword;
    }
}
