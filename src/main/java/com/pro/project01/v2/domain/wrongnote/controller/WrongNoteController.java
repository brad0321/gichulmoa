package com.pro.project01.v2.domain.wrongnote.controller;

import com.pro.project01.v2.domain.wrongnote.dto.WrongNoteCreateRequest;
import com.pro.project01.v2.domain.wrongnote.dto.WrongNoteResponse;
import com.pro.project01.v2.domain.wrongnote.dto.WrongNoteUpdateRequest;
import com.pro.project01.v2.domain.wrongnote.service.WrongNoteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wrong-notes")
public class WrongNoteController {

    private final WrongNoteService wrongNoteService;

    /**
     * ✅ 사용자 오답노트 목록
     */
    @GetMapping
    public String list(@RequestParam("userId") Long userId, Model model) {
        List<WrongNoteResponse> notes = wrongNoteService.findByUserId(userId);
        model.addAttribute("notes", notes);
        return "wrongnotes/list"; // 뷰 템플릿 필요
    }

    /**
     * ✅ 오답노트 상세
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        WrongNoteResponse note = wrongNoteService.findById(id);
        model.addAttribute("note", note);
        model.addAttribute("loginUser", session.getAttribute("loginUser"));
        return "wrongnotes/detail";
    }

    /**
     * ✅ 오답노트 등록 처리
     */
    @PostMapping("/new")
    public String create(@ModelAttribute WrongNoteCreateRequest request) {
        Long id = wrongNoteService.create(request);
        return "redirect:/wrong-notes/" + id;
    }

    /**
     * ✅ 오답노트 수정 처리
     */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute WrongNoteUpdateRequest request) {
        wrongNoteService.update(id, request);
        return "redirect:/wrong-notes/" + id;
    }

    /**
     * ✅ 오답노트 삭제
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        wrongNoteService.delete(id);
        return "redirect:/dashboard";
    }
}
