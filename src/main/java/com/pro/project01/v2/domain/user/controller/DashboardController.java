package com.pro.project01.v2.domain.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // ✅ 회원(UserResponse) 또는 게스트(GuestSessionUser) 모두 허용
        Object principal = session.getAttribute("loginUser");
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", principal); // 캐스팅 금지

        // ✅ 시험일: 2025-10-25 09:00 (음수 방지)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examDate = LocalDateTime.of(2025, 10, 25, 9, 0);

        Duration duration = Duration.between(now, examDate);
        long totalSecs = Math.max(0, duration.getSeconds()); // 음수 방지
        long daysLeft  = totalSecs / 86_400;
        long hoursLeft = (totalSecs % 86_400) / 3_600;

        model.addAttribute("daysLeft", daysLeft);
        model.addAttribute("hoursLeft", hoursLeft);

        return "dashboard";
    }
}
