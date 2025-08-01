package com.pro.project01.v2.domain.problem.entity;

public enum ProblemType {
    PAST("기출문제"),
    OX("OX퀴즈");

    private final String description;

    ProblemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
