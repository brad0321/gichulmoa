// src/main/java/com/pro/project01/v2/domain/problem/dto/ProblemListItemView.java
package com.pro.project01.v2.domain.problem.dto;

import java.time.LocalDateTime;

public interface ProblemListItemView {

    Long getId();

    String getTitle();

    Long getSubjectId();
    Long getRoundId();
    Long getUnitId();

    String getSubjectName();
    String getRoundName();
    Integer getRoundNumber();
    String getUnitName();

    Integer getRoundProblemNo();

    /** ✅ 과목 내 고유 번호 (Generated Column: subject_problem_no) */
    Integer getSubjectProblemNo();

    LocalDateTime getCreatedAt();
}
