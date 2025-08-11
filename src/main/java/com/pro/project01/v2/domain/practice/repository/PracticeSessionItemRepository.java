package com.pro.project01.v2.domain.practice.repository;

import com.pro.project01.v2.domain.practice.entity.PracticeSessionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeSessionItemRepository extends JpaRepository<PracticeSessionItem, Long> {
    List<PracticeSessionItem> findBySession_IdOrderByOrderIndexAsc(Long sessionId);
}
