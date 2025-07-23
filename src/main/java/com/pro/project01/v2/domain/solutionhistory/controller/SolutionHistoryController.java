package com.pro.project01.v2.domain.solutionhistory.controller;

import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryCreateRequest;
import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryResponse;
import com.pro.project01.v2.domain.solutionhistory.dto.SolutionHistoryUpdateRequest;
import com.pro.project01.v2.domain.solutionhistory.service.SolutionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v2/histories")
public class SolutionHistoryController {

    private final SolutionHistoryService service;

    // ✅ 목록: 사용자 기준
    @GetMapping("/user/{userId}")
    public String listByUser(@PathVariable Long userId, Model model) {
        List<SolutionHistoryResponse> list = service.findByUser(userId);
        model.addAttribute("histories", list);
        return "v2/histories/list-by-user";
    }

    // ✅ 목록: 문제 기준
    @GetMapping("/problem/{problemId}")
    public String listByProblem(@PathVariable Long problemId, Model model) {
        List<SolutionHistoryResponse> list = service.findByProblem(problemId);
        model.addAttribute("histories", list);
        return "v2/histories/list-by-problem";
    }

    // ✅ 단건 보기
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        SolutionHistoryResponse h = service.findById(id);
        model.addAttribute("history", h);
        return "v2/histories/detail";
    }

    // ✅ 등록 폼
    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("request", new SolutionHistoryCreateRequest(null, null, false));
        return "v2/histories/form-new";
    }

    // ✅ 등록 처리
    @PostMapping("/new")
    public String create(@ModelAttribute SolutionHistoryCreateRequest request) {
        Long id = service.create(request);
        return "redirect:/v2/histories/" + id;
    }

    // ✅ 정답 여부 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        SolutionHistoryResponse h = service.findById(id);
        model.addAttribute("history", h);
        model.addAttribute("request", new SolutionHistoryUpdateRequest(h.isCorrect()));
        return "v2/histories/form-edit";
    }

    // ✅ 정답 여부 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute SolutionHistoryUpdateRequest request) {
        service.updateCorrect(id, request);
        return "redirect:/v2/histories/" + id;
    }

    // ✅ 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/v2/histories/user/1"; // 예시로 user 1의 목록으로 이동
    }
}
