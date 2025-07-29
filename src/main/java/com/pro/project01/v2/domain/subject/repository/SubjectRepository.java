package com.pro.project01.v2.domain.subject.repository;

import com.pro.project01.v2.domain.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
