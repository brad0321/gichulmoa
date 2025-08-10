package com.pro.project01.v2.domain.user.controller;

import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.global.session.GuestSessionUser;
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
        Object principal = session.getAttribute("loginUser");
        if (principal == null) return "redirect:/login";

        model.addAttribute("loginUser", principal);

        boolean isGuest;
        String displayName;

        if (principal instanceof UserResponse user) {
            isGuest = false;
            displayName = user.username();
        } else if (principal instanceof GuestSessionUser guest) {
            isGuest = true;
            displayName = guest.getUsername(); // ✅ 필드명에 맞춰 변경
        } else {
            isGuest = true;
            displayName = "게스트";
        }

        model.addAttribute("isGuest", isGuest);
        model.addAttribute("displayName", displayName);

        // 시험일: 2025-10-25 09:30
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examDate = LocalDateTime.of(2025, 10, 25, 9, 30);
        long totalSecs = Math.max(0, Duration.between(now, examDate).getSeconds());
        model.addAttribute("daysLeft",  totalSecs / 86_400);
        model.addAttribute("hoursLeft", (totalSecs % 86_400) / 3_600);

        return "dashboard";
    }
}
