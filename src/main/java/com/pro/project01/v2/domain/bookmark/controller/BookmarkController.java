package com.pro.project01.v2.domain.bookmark.controller;

import com.pro.project01.v2.domain.bookmark.dto.BookmarkCreateRequest;
import com.pro.project01.v2.domain.bookmark.dto.BookmarkResponse;
import com.pro.project01.v2.domain.bookmark.dto.BookmarkUpdateRequest;
import com.pro.project01.v2.domain.bookmark.service.BookmarkService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // ✅ 북마크 등록 폼
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("bookmark", new BookmarkCreateRequest(null, null, ""));
        return "bookmarks/form-new";
    }

    // ✅ 북마크 등록 처리
    @PostMapping("/new")
    public String create(@ModelAttribute BookmarkCreateRequest request) {
        Long id = bookmarkService.create(request);
        return "redirect:/bookmarks/" + id;
    }

    // ✅ 북마크 목록 (by userId)
    @GetMapping
    public String list(@RequestParam Long userId, Model model) {
        List<BookmarkResponse> bookmarks = bookmarkService.findByUserId(userId);
        model.addAttribute("bookmarks", bookmarks);
        return "bookmarks/list";
    }

    // ✅ 북마크 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        BookmarkResponse bookmark = bookmarkService.findById(id);
        model.addAttribute("bookmark", bookmark);
        model.addAttribute("loginUser", session.getAttribute("loginUser"));
        return "bookmarks/detail";
    }

    // ✅ 북마크 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        BookmarkResponse bookmark = bookmarkService.findById(id);
        model.addAttribute("bookmark", bookmark);
        return "bookmarks/form-edit";
    }

    // ✅ 북마크 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute BookmarkUpdateRequest request) {
        bookmarkService.update(id, request);
        return "redirect:/bookmarks/" + id;
    }

    // ✅ 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        bookmarkService.delete(id);
        return "redirect:/dashboard";
    }
}
