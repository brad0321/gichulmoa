package com.pro.project01.v2.domain.bookmark.service;

import com.pro.project01.v2.domain.bookmark.dto.BookmarkCreateRequest;
import com.pro.project01.v2.domain.bookmark.dto.BookmarkResponse;
import com.pro.project01.v2.domain.bookmark.dto.BookmarkUpdateRequest;
import com.pro.project01.v2.domain.bookmark.entity.Bookmark;
import com.pro.project01.v2.domain.bookmark.repository.BookmarkRepository;
import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    @Override
    public Long create(BookmarkCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("문제 없음"));

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .problem(problem)
                .tag(request.tag())
                .build();

        return bookmarkRepository.save(bookmark).getId();
    }

    @Override
    public void update(Long id, BookmarkUpdateRequest request) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("북마크 없음"));
        bookmark.updateTag(request.tag());
    }

    @Override
    public void delete(Long id) {
        bookmarkRepository.deleteById(id);
    }

    @Override
    public BookmarkResponse findById(Long id) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("북마크 없음"));
        return toResponse(bookmark);
    }

    @Override
    public List<BookmarkResponse> findByUserId(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private BookmarkResponse toResponse(Bookmark bookmark) {
        return new BookmarkResponse(
                bookmark.getId(),
                bookmark.getUser().getId(),
                bookmark.getProblem().getId(),
                bookmark.getTag(),
                bookmark.getCreatedAt()
        );
    }
}
