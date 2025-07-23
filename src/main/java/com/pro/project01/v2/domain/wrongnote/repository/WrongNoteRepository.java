package com.pro.project01.v2.domain.wrongnote.repository;

import com.pro.project01.v2.domain.wrongnote.entity.WrongNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WrongNoteRepository extends JpaRepository<WrongNote, Long> {

    List<WrongNote> findByUserId(Long userId);

    boolean existsByUserIdAndProblemId(Long userId, Long problemId);
}
