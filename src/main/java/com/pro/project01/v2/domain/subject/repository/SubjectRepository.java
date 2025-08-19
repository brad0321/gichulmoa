package com.pro.project01.v2.domain.subject.repository;

import com.pro.project01.v2.domain.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * 과목명이 존재하는지 확인하기 위해 이름으로 조회하는 메서드.
     */
    Optional<Subject> findByName(String name);
}
