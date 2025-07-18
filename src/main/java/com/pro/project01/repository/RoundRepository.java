package com.pro.project01.repository;

import com.pro.project01.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> {
    Round findByName(String name); // 필요 시
}
