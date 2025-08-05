package com.pro.project01.v2.domain.calculation.controller;

import com.pro.project01.v2.domain.calculation.dto.CalculationProblemResponse;
import com.pro.project01.v2.domain.calculation.service.CalculationProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calculation-problems")
public class CalculationProblemController {

    private final CalculationProblemService calculationProblemService;

    /**
     * ✅ 계산문제 전체 목록
     */
    @GetMapping
    public String list(Model model) {
        List<CalculationProblemResponse> problems = calculationProblemService.findAll();
        model.addAttribute("calculationProblems", problems);
        return "calculation/list"; // 뷰 파일: templates/calculation/list.html
    }

    /**
     * ✅ 계산문제 상세보기
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        CalculationProblemResponse problem = calculationProblemService.findById(id);
        model.addAttribute("calculationProblem", problem);
        return "calculation/detail"; // 뷰 파일: templates/calculation/detail.html
    }

    /**
     * ✅ 삭제
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        calculationProblemService.delete(id);
        return "redirect:/calculation-problems";
    }
}
