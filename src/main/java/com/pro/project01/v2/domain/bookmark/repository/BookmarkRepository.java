package com.pro.project01.v2.domain.bookmark.repository;

import com.pro.project01.v2.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUserId(Long userId);

    Optional<Bookmark> findByUserIdAndProblemId(Long userId, Long problemId);

    boolean existsByUserIdAndProblemId(Long userId, Long problemId);
}
