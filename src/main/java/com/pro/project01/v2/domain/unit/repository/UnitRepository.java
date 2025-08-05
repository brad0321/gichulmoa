package com.pro.project01.v2.domain.unit.repository;

import com.pro.project01.v2.domain.unit.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findBySubject_Id(Long subjectId);
}
