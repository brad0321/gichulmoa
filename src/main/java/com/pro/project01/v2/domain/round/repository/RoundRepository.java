package com.pro.project01.v2.domain.round.repository;

import com.pro.project01.v2.domain.round.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> {
}
