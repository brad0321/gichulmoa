package com.pro.project01.v2.domain.bookmark.service;

import com.pro.project01.v2.domain.bookmark.dto.BookmarkCreateRequest;
import com.pro.project01.v2.domain.bookmark.dto.BookmarkResponse;
import com.pro.project01.v2.domain.bookmark.dto.BookmarkUpdateRequest;

import java.util.List;

public interface BookmarkService {

    Long create(BookmarkCreateRequest request);

    void update(Long id, BookmarkUpdateRequest request);

    void delete(Long id);

    BookmarkResponse findById(Long id);

    List<BookmarkResponse> findByUserId(Long userId);
}
