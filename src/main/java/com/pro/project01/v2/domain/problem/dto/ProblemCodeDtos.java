// src/main/java/com/pro/project01/v2/domain/problem/dto/ProblemCodeDtos.java
package com.pro.project01.v2.domain.problem.dto;

import jakarta.validation.constraints.*;

public class ProblemCodeDtos {

    public record UpdateCodesRequest(
            @NotNull @Min(1) @Max(40) Integer roundProblemNo,
            @NotBlank @Pattern(regexp="^\\d{2}$") String subjectCode,
            @NotBlank @Pattern(regexp="^\\d{3}$") String unitSeqCode,
            @NotNull @Min(1) @Max(99) Integer unitProblemNo
    ){ }

    public record CodesResponse(
            Long id,
            Integer roundProblemNo,
            String subjectCode,
            String unitSeqCode,
            Integer unitProblemNo,
            String roundCode,   // DB 생성컬럼(읽기전용)
            String unitCode     // DB 생성컬럼(읽기전용)
    ){ }
}
