package com.pro.project01.v2.domain.scorestat.repository;

import com.pro.project01.v2.domain.scorestat.entity.ScoreStat;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface ScoreStatRepository extends JpaRepository<ScoreStat, Long> {

    // ✅ 특정 사용자 점수 기록 조회
    List<ScoreStat> findByUserId(Long userId);

    // ✅ 특정 사용자 + 과목 + 유형 + 회차로 검색 (중복 기록 방지 시 사용 가능)
    boolean existsByUserIdAndSubjectIdAndRoundId(Long userId, Long subjectId, Long roundId);

    /* 유저 + 과목 + 회차 + 유형 조합 1건 조회 (없으면 빈 Optional) */
    Optional<ScoreStat> findByUserAndSubjectAndRound(
            User user,
            Subject subject,
            Round round);
}


