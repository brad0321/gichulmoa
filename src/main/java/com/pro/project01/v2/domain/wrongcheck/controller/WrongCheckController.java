package com.pro.project01.v2.domain.wrongcheck.controller;

import com.pro.project01.v2.domain.wrongcheck.dto.WrongCheckCreateRequest;
import com.pro.project01.v2.domain.wrongcheck.dto.WrongCheckResponse;
import com.pro.project01.v2.domain.wrongcheck.service.WrongCheckService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wrong-checks")
public class WrongCheckController {

    private final WrongCheckService wrongCheckService;

    /**
     * ✅ 오답 체크 목록 (사용자별)
     */
    @GetMapping
    public String list(@RequestParam Long userId, Model model) {
        List<WrongCheckResponse> wrongChecks = wrongCheckService.findByUserId(userId);
        model.addAttribute("wrongChecks", wrongChecks);
        return "wrongchecks/list"; // 뷰 파일명 (v2/templates/wrongchecks/list.html 예정)
    }

    /**
     * ✅ 오답 체크 등록 처리
     */
    @PostMapping("/new")
    public String create(@ModelAttribute WrongCheckCreateRequest request) {
        Long id = wrongCheckService.create(request);
        return "redirect:/wrong-checks/" + id;
    }

    /**
     * ✅ 오답 체크 상세 보기
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        WrongCheckResponse wrongCheck = wrongCheckService.findById(id);
        model.addAttribute("wrongCheck", wrongCheck);
        model.addAttribute("loginUser", session.getAttribute("loginUser"));
        return "wrongchecks/detail";
    }

    /**
     * ✅ 삭제 처리
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        wrongCheckService.delete(id);
        return "redirect:/dashboard";
    }
}
