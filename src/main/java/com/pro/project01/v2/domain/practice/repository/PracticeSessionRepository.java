package com.pro.project01.v2.domain.practice.repository;

import com.pro.project01.v2.domain.practice.entity.PracticeSession;
import com.pro.project01.v2.domain.practice.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeSessionRepository extends JpaRepository<PracticeSession, Long> {
    List<PracticeSession> findByUser_IdAndStatus(Long userId, SessionStatus status);
}
