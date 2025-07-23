package com.pro.project01.v2.domain.problemchoice.controller;

import com.pro.project01.v2.domain.problemchoice.dto.*;
import com.pro.project01.v2.domain.problemchoice.service.ProblemChoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v2/problem-choices")
public class ProblemChoiceController {

    private final ProblemChoiceService problemChoiceService;

    // ✅ 특정 문제의 보기 리스트 보기 (관리자 전용)
    @GetMapping("/problem/{problemId}")
    public String list(@PathVariable Long problemId, Model model) {
        List<ProblemChoiceResponse> choices = problemChoiceService.findByProblemId(problemId);
        model.addAttribute("choices", choices);
        model.addAttribute("problemId", problemId);
        return "problems/choice-list"; // 필요 시 생성
    }

    // ✅ 보기 등록 폼
    @GetMapping("/new")
    public String showCreateForm(@RequestParam Long problemId, Model model) {
        model.addAttribute("problemId", problemId);
        model.addAttribute("request", new ProblemChoiceCreateRequest(problemId, "", false));
        return "problems/choice-new"; // 필요 시 생성
    }

    // ✅ 등록 처리
    @PostMapping("/new")
    public String create(@ModelAttribute ProblemChoiceCreateRequest request) {
        problemChoiceService.create(request);
        return "redirect:/v2/problem-choices/problem/" + request.problemId();
    }

    // ✅ 보기 수정 폼
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProblemChoiceResponse choice = problemChoiceService.findById(id);
        model.addAttribute("choice", choice);
        return "problems/choice-edit"; // 필요 시 생성
    }

    // ✅ 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute ProblemChoiceUpdateRequest request) {
        problemChoiceService.update(id, request);
        return "redirect:/v2/problem-choices";
    }

    // ✅ 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        problemChoiceService.delete(id);
        return "redirect:/v2/problem-choices";
    }
}
