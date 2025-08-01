package com.pro.project01.v2.domain.user.controller;

import com.pro.project01.v2.domain.user.dto.UserResponse;
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
        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // ✅ 시험일: 2025년 10월 25일 09:00
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examDate = LocalDateTime.of(2025, 10, 25, 9, 0);

        Duration duration = Duration.between(now, examDate);
        long totalHours = duration.toHours();
        long daysLeft = duration.toDays();
        long hoursLeft = totalHours % 24;

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("daysLeft", daysLeft);
        model.addAttribute("hoursLeft", hoursLeft);

        return "dashboard";
    }
}
