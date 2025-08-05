package com.pro.project01.v2.domain.wrongproblem.controller;

import com.pro.project01.v2.domain.wrongproblem.dto.WrongProblemCreateRequest;
import com.pro.project01.v2.domain.wrongproblem.dto.WrongProblemResponse;
import com.pro.project01.v2.domain.wrongproblem.dto.WrongProblemUpdateRequest;
import com.pro.project01.v2.domain.wrongproblem.service.WrongProblemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wrong-problems")
public class WrongProblemController {

    private final WrongProblemService wrongProblemService;

    /**
     * ✅ 틀린문제 목록 (사용자별)
     */
    @GetMapping
    public String list(@RequestParam Long userId, Model model) {
        List<WrongProblemResponse> wrongProblems = wrongProblemService.findByUserId(userId);
        model.addAttribute("wrongProblems", wrongProblems);
        return "wrongproblems/list"; // ✅ 뷰 파일명
    }

    /**
     * ✅ 틀린문제 등록 처리
     */
    @PostMapping("/new")
    public String create(@ModelAttribute WrongProblemCreateRequest request) {
        Long id = wrongProblemService.create(request);
        return "redirect:/wrong-problems/" + id;
    }

    /**
     * ✅ 틀린문제 상세 보기
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        WrongProblemResponse wrongProblem = wrongProblemService.findById(id);
        model.addAttribute("wrongProblem", wrongProblem);
        model.addAttribute("loginUser", session.getAttribute("loginUser"));
        return "wrongproblems/detail";
    }

    /**
     * ✅ 수정 처리
     */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute WrongProblemUpdateRequest request) {
        wrongProblemService.update(id, request);
        return "redirect:/wrong-problems/" + id;
    }

    /**
     * ✅ 삭제 처리
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        wrongProblemService.delete(id);
        return "redirect:/dashboard";
    }
}
