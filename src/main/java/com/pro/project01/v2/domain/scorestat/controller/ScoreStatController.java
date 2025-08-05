package com.pro.project01.v2.domain.scorestat.controller;

import com.pro.project01.v2.domain.scorestat.dto.ScoreStatCreateRequest;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatResponse;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatUpdateRequest;
import com.pro.project01.v2.domain.scorestat.service.ScoreStatService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/score-stats")
public class ScoreStatController {

    private final ScoreStatService scoreStatService;

    /**
     * ✅ 점수 통계 전체 목록
     */
    @GetMapping
    public String list(Model model) {
        List<ScoreStatResponse> stats = scoreStatService.findAll();
        model.addAttribute("stats", stats);
        return "scorestats/list"; // 뷰 템플릿 필요
    }

    /**
     * ✅ 특정 사용자 점수 통계
     */
    @GetMapping("/user")
    public String userStats(@RequestParam Long userId, Model model) {
        List<ScoreStatResponse> stats = scoreStatService.findByUserId(userId);
        model.addAttribute("stats", stats);
        return "scorestats/user-list"; // 뷰 템플릿 필요
    }

    /**
     * ✅ 점수 통계 상세 보기
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ScoreStatResponse stat = scoreStatService.findById(id);
        model.addAttribute("stat", stat);
        return "scorestats/detail"; // 뷰 템플릿 필요
    }

    /**
     * ✅ 점수 통계 등록
     */
    @PostMapping("/new")
    public String create(@ModelAttribute ScoreStatCreateRequest request, HttpSession session) {
        scoreStatService.create(request);
        return "redirect:/score-stats/user?userId=" + request.userId();
    }

    /**
     * ✅ 점수 통계 수정
     */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute ScoreStatUpdateRequest request) {
        scoreStatService.update(id, request);
        return "redirect:/score-stats/" + id;
    }

    /**
     * ✅ 점수 통계 삭제
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        scoreStatService.delete(id);
        return "redirect:/score-stats";
    }
}
