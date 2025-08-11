package com.pro.project01.v2.domain.explanation.controller;

import com.pro.project01.v2.domain.explanation.dto.ExplanationCreateRequest;
import com.pro.project01.v2.domain.explanation.dto.ExplanationResponse;
import com.pro.project01.v2.domain.explanation.dto.ExplanationUpdateRequest;
import com.pro.project01.v2.domain.explanation.service.ExplanationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/explanations")
public class ExplanationController {

    private final ExplanationService explanationService;

    // ✅ 특정 문제의 해설 목록
    @GetMapping("/problem/{problemId}")
    public String listByProblem(@PathVariable Long problemId, Model model) {
        List<ExplanationResponse> explanations = explanationService.findByProblemId(problemId);
        model.addAttribute("explanations", explanations);
        model.addAttribute("problemId", problemId);
        return "explanations/list";
    }

    // ✅ 해설 작성 폼
    @GetMapping("/new/{problemId}")
    public String createForm(@PathVariable Long problemId, Model model) {
        model.addAttribute("problemId", problemId);
        return "explanations/create";
    }

    // ✅ 해설 저장
    @PostMapping
    public String create(@ModelAttribute ExplanationCreateRequest request) {
        explanationService.create(request);
        return "redirect:/explanations/problem/" + request.getProblemId();
    }

    // ✅ 해설 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ExplanationResponse explanation = explanationService.findById(id);
        model.addAttribute("explanation", explanation);
        return "explanations/edit";
    }

    // ✅ 해설 수정
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ExplanationUpdateRequest request) {
        explanationService.update(id, request);
        return "redirect:/explanations/problem/" + request.getProblemId();
    }

    // ✅ 해설 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam Long problemId) {
        explanationService.delete(id);
        return "redirect:/explanations/problem/" + problemId;
    }
}
