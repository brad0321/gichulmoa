package com.pro.project01.v2.domain.round.repository;

import com.pro.project01.v2.domain.round.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findBySubject_Id(Long subjectId);
}
