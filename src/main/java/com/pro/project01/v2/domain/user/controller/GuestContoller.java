package com.pro.project01.v2.domain.user.controller;

import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.domain.user.entity.Grade;
import com.pro.project01.v2.domain.user.entity.Role;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/guest")
public class GuestContoller
{
    @GetMapping("/start")
    public String guestLogin(HttpSession session, Model model)
    {
        UserResponse guestUser = new UserResponse(
                0L,
                "guest@example.com",
                "guest",
                Grade.GUEST,
                Role.USER,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        session.setAttribute("loginUser", guestUser);
        model.addAttribute("loginUser", guestUser);
        return "redirect:/dashboard";
    }
}
