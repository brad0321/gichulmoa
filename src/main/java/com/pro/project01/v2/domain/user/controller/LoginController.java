package com.pro.project01.v2.domain.user.controller;

import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping({"/", "/login"})
    public String loginForm() {
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            UserResponse user = userService.login(username, password);
            session.setAttribute("loginUser", user);
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
