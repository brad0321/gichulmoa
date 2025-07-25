package com.pro.project01.v2.domain.problem.controller;

import com.pro.project01.v2.domain.problem.dto.ProblemCreateRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.dto.ProblemUpdateRequest;
import com.pro.project01.v2.domain.problem.repository.RoundRepository;
import com.pro.project01.v2.domain.problem.repository.SubjectRepository;
import com.pro.project01.v2.domain.problem.repository.UnitRepository;
import com.pro.project01.v2.domain.problem.service.ProblemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    // ✅ 문제 등록 폼
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", roundRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "problems/problem-new";
    }

    // ✅ 문제 등록 처리
    @PostMapping("/new")
    public String createProblem(@ModelAttribute ProblemCreateRequest request) {
        Long id = problemService.create(request);
        return "redirect:/problems/" + id;
    }

    // ✅ 문제 목록
    @GetMapping
    public String list(Model model) {
        List<ProblemResponse> problems = problemService.findAll();
        model.addAttribute("problems", problems);
        return "problems/list";
    }

    // ✅ 문제 상세 보기
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        ProblemResponse problem = problemService.findById(id);
        model.addAttribute("problem", problem);
        model.addAttribute("loginUser", session.getAttribute("loginUser"));
        return "problems/detail";
    }

    // ✅ 문제 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ProblemResponse problem = problemService.findById(id);
        model.addAttribute("problem", problem);
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", roundRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "problems/edit";
    }

    // ✅ 문제 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute ProblemUpdateRequest request,
                         @RequestParam(value = "viewImage", required = false) MultipartFile viewImage) {
        problemService.update(id, request, viewImage);
        return "redirect:/problems/" + id;
    }

    // ✅ 문제 삭제
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        problemService.delete(id);
        return "redirect:/problems";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        problemService.delete(id);
        return "redirect:/problems";
    }

    // ✅ 문제 풀이 화면
    @GetMapping("/solve")
    public String solve(Model model) {
        List<ProblemResponse> problems = problemService.findAll();
        model.addAttribute("problems", problems);
        return "problems/solve";
    }
}
