package com.pro.project01.v1.repository;

import com.pro.project01.v1.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByName(String name); // 필요 시
}
