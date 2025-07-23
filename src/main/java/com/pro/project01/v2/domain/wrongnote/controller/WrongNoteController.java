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
@RequestMapping("/v2/wrong-notes")
public class WrongNoteController {

    private final WrongNoteService wrongNoteService;

    // ✅ 사용자 오답노트 목록
    @GetMapping
    public String list(@RequestParam("userId") Long userId, Model model) {
        List<WrongNoteResponse> notes = wrongNoteService.findByUserId(userId);
        model.addAttribute("notes", notes);
        return "v2/wrongnotes/list";
    }

    // ✅ 오답노트 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        WrongNoteResponse note = wrongNoteService.findById(id);
        model.addAttribute("note", note);
        return "v2/wrongnotes/detail";
    }

    // ✅ 오답노트 등록 폼
    @GetMapping("/new")
    public String newForm(@RequestParam Long userId,
                          @RequestParam Long problemId,
                          Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("problemId", problemId);
        return "v2/wrongnotes/form-new";
    }

    // ✅ 오답노트 등록 처리
    @PostMapping("/new")
    public String create(@ModelAttribute WrongNoteCreateRequest request) {
        Long id = wrongNoteService.create(request);
        return "redirect:/v2/wrong-notes/" + id;
    }

    // ✅ 오답노트 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        WrongNoteResponse note = wrongNoteService.findById(id);
        model.addAttribute("note", note);
        return "v2/wrongnotes/form-edit";
    }

    // ✅ 오답노트 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute WrongNoteUpdateRequest request) {
        wrongNoteService.update(id, request);
        return "redirect:/v2/wrong-notes/" + id;
    }

    // ✅ 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        wrongNoteService.delete(id);
        return "redirect:/v2/wrong-notes";
    }
}
