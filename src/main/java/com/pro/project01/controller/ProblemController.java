package com.pro.project01.controller;

import com.pro.project01.entity.ProblemForm;
import com.pro.project01.entity.Problem;
import com.pro.project01.service.ProblemService;
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

    @GetMapping("/problems/new")
    public String showCreateForm(Model model) {
        model.addAttribute("form", problemService.createForm());
        model.addAttribute("subjects", problemService.getSubjects());
        model.addAttribute("rounds", problemService.getRounds());
        model.addAttribute("units", problemService.getUnits());
        return "problems/problem-new";
    }

    @PostMapping("/problems/new")
    public String saveProblem(@ModelAttribute ProblemForm form) throws IOException {
        Problem savedProblem = problemService.saveProblem(form);
        return "redirect:/problems/" + savedProblem.getId();
    }

    @GetMapping("/problems")
    public String listProblems(Model model, HttpSession session) {
        model.addAttribute("problems", problemService.findAllProblems());
        model.addAttribute("loginUser", session.getAttribute("loginUser"));
        return "problems/list";
    }

    @GetMapping("/problems/{id}")
    public String detailProblem(@PathVariable Long id, Model model, HttpSession session) {
        model.addAttribute("problem", problemService.findProblemById(id));
        model.addAttribute("loginUser", session.getAttribute("loginUser"));
        return "problems/detail";
    }

    @GetMapping("/solve")
    public String solveProblems(Model model) {
        model.addAttribute("problems", problemService.findAllProblems());
        return "problems/solve";
    }

    @PostMapping("/problems/{id}/delete")
    public String deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return "redirect:/problems";
    }
}