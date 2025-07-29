package com.pro.project01.v2.domain.user.controller;

import com.pro.project01.v2.domain.user.dto.UserRequest;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class UserController {

    private final UserService userService;

    // ✅ 회원가입 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new UserRequest(null, null, null));
        return "members/new";
    }

    // ✅ 회원가입 처리 + 자동 로그인
    @PostMapping("/new")
    public String create(@ModelAttribute UserRequest request, HttpSession session) {
        // 1) 회원 저장
        UserResponse newUser = userService.register(request);

        // 2) 자동 로그인 세션 설정
        session.setAttribute("loginUser", newUser);

        // 3) 대시보드로 리다이렉트
        return "redirect:/dashboard";
    }
}
