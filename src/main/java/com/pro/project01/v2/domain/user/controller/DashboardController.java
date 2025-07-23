package com.pro.project01.v2.domain.user.controller;

import com.pro.project01.v2.domain.user.dto.UserResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController
{
    @GetMapping
    public String dashboard(HttpSession session, Model model)
    {
        UserResponse loginUser = (UserResponse)session.getAttribute("loginUser");
        if(loginUser == null) return "redirect:/login";

        model.addAttribute("loginUser", loginUser);
        return "dashboard";
    }
}
