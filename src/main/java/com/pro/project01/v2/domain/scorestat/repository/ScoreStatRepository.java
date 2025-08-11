package com.pro.project01.v2.domain.scorestat.repository;

import com.pro.project01.v2.domain.scorestat.entity.ScoreStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreStatRepository extends JpaRepository<ScoreStat, Long> {
    Optional<ScoreStat> findByUser_IdAndSubject_IdAndRound_Id(Long userId, Long subjectId, Long roundId);
    List<ScoreStat> findByUser_Id(Long userId);

}
