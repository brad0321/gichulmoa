package com.pro.project01.v1.controller;

import com.pro.project01.v1.entity.ProblemForm;
import com.pro.project01.v1.entity.Problem;
import com.pro.project01.v1.service.ProblemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    // 문제 등록 폼
    @GetMapping("/problems/new")
    public String showCreateForm(Model model) {
        model.addAttribute("form", problemService.createForm());
        model.addAttribute("subjects", problemService.getSubjects());
        model.addAttribute("rounds", problemService.getRounds());
        model.addAttribute("units", problemService.getUnits());
        return "problems/problem-new";
    }

    // 문제 저장 처리
    @PostMapping("/problems/new")
    public String saveProblem(@ModelAttribute ProblemForm form) throws IOException {
        Problem savedProblem = problemService.saveProblem(form);
        return "redirect:/problems/" + savedProblem.getId();
    }

    // 문제 목록
    @GetMapping("/problems")
    public String listProblems(Model model, HttpSession session) {
        model.addAttribute("problems", problemService.findAllProblems());
        model.addAttribute("loginUser", session.getAttribute("loginUser"));
        return "problems/list";
    }

    // 문제 상세
    @GetMapping("/problems/{id}")
    public String detailProblem(@PathVariable Long id, Model model, HttpSession session) {
        model.addAttribute("problem", problemService.findProblemById(id));

        // ✅ 세션의 로그인 사용자도 항상 넣어줌 (버튼 제어용)
        model.addAttribute("loginUser", session.getAttribute("loginUser"));

        return "problems/detail";
    }

    // 문제 수정 폼
    @GetMapping("/problems/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Problem problem = problemService.findProblemById(id);
        model.addAttribute("problem", problem);
        model.addAttribute("subjects", problemService.getSubjects());
        model.addAttribute("rounds", problemService.getRounds());
        model.addAttribute("units", problemService.getUnits());
        return "problems/edit";
    }

    // 문제 수정 처리
    @PostMapping("/problems/{id}/edit")
    public String updateProblem(@PathVariable Long id, @ModelAttribute ProblemForm form) throws IOException {
        problemService.updateProblem(id, form);
        return "redirect:/problems/" + id;
    }

    // 문제 삭제
    @PostMapping("/problems/{id}/delete")
    public String deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return "redirect:/problems";
    }

    // 문제 풀이
    @GetMapping("/solve")
    public String solveProblems(Model model) {
        model.addAttribute("problems", problemService.findAllProblems());
        return "problems/solve";
    }
}
