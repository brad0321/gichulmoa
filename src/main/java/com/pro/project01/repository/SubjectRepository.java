package com.pro.project01.repository;

import com.pro.project01.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Subject findByName(String name); // 이름으로도 조회 가능하게 (선택)
}
