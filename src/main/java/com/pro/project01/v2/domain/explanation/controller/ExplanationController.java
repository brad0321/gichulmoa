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
@RequestMapping("/v2/explanations")
public class ExplanationController {

    private final ExplanationService explanationService;

    // ✅ 특정 문제의 해설 목록
    @GetMapping("/problem/{problemId}")
    public String listByProblem(@PathVariable Long problemId, Model model) {
        List<ExplanationResponse> explanations = explanationService.findByProblemId(problemId);
        model.addAttribute("explanations", explanations);
        model.addAttribute("problemId", problemId);
        return "v2/explanations/list";
    }

    // ✅ 해설 등록 폼
    @GetMapping("/new")
    public String showCreateForm(@RequestParam Long problemId, Model model) {
        ExplanationCreateRequest request = new ExplanationCreateRequest(problemId, null, null);
        model.addAttribute("explanation", request);
        return "v2/explanations/form-new";
    }

    // ✅ 해설 등록 처리
    @PostMapping("/new")
    public String create(@ModelAttribute ExplanationCreateRequest request) {
        explanationService.create(request);
        return "redirect:/v2/explanations/problem/" + request.problemId();
    }

    // ✅ 해설 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ExplanationResponse explanation = explanationService.findById(id);
        model.addAttribute("explanation", explanation);
        return "v2/explanations/form-edit";
    }

    // ✅ 해설 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute ExplanationUpdateRequest request,
                         @RequestParam Long problemId) {
        explanationService.update(id, request);
        return "redirect:/v2/explanations/problem/" + problemId;
    }

    // ✅ 해설 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam Long problemId) {
        explanationService.delete(id);
        return "redirect:/v2/explanations/problem/" + problemId;
    }
}
