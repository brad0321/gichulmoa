package com.pro.project01.v1.controller;

import com.pro.project01.v1.entity.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("loginUser");

        if (member == null) {
            return "redirect:/";
        }

        model.addAttribute("username", member.getUsername());
        model.addAttribute("role", member.getRole());
        model.addAttribute("loginUser", member);
        return "dashboard";
    }
}

