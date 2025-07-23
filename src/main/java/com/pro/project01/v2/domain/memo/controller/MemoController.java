package com.pro.project01.v2.domain.memo.controller;

import com.pro.project01.v2.domain.memo.dto.MemoCreateRequest;
import com.pro.project01.v2.domain.memo.dto.MemoResponse;
import com.pro.project01.v2.domain.memo.dto.MemoUpdateRequest;
import com.pro.project01.v2.domain.memo.service.MemoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v2/memos")
public class MemoController {

    private final MemoService memoService;

    // ✅ 메모 목록 (사용자별)
    @GetMapping
    public String list(Model model, HttpSession session) {
        Long userId = getLoginUserId(session);
        List<MemoResponse> memos = memoService.findByUserId(userId);
        model.addAttribute("memos", memos);
        return "v2/memos/list";
    }

    // ✅ 메모 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        MemoResponse memo = memoService.findById(id);
        model.addAttribute("memo", memo);
        return "v2/memos/detail";
    }

    // ✅ 메모 등록 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("memo", new MemoCreateRequest(null, null, ""));
        return "v2/memos/form-new";
    }

    // ✅ 메모 등록 처리
    @PostMapping("/new")
    public String create(@ModelAttribute MemoCreateRequest request) {
        Long id = memoService.create(request);
        return "redirect:/v2/memos/" + id;
    }

    // ✅ 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        MemoResponse memo = memoService.findById(id);
        model.addAttribute("memo", memo);
        return "v2/memos/form-edit";
    }

    // ✅ 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute MemoUpdateRequest request) {
        memoService.update(id, request);
        return "redirect:/v2/memos/" + id;
    }

    // ✅ 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        memoService.delete(id);
        return "redirect:/v2/memos";
    }

    private Long getLoginUserId(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) throw new IllegalStateException("로그인 정보가 없습니다.");
        return ((com.pro.project01.v2.domain.user.entity.User) loginUser).getId();
    }
}
