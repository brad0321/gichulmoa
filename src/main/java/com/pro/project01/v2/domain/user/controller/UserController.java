package com.pro.project01.v2.domain.user.controller;

import com.pro.project01.v2.domain.user.dto.*;
import com.pro.project01.v2.domain.user.service.UserService;
import com.pro.project01.v2.global.session.GuestSessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final SecureRandom RND = new SecureRandom();
    private static final String SESSION_KEY = "loginUser";

    /* ===================== 로그인 ===================== */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) model.addAttribute("loginError", "아이디 또는 비밀번호를 확인하세요.");
        return "index"; // 로그인 뷰
    }

    @PostMapping("/login")
    public String doLogin(@Valid UserLoginRequest req, HttpSession session, Model model) {
        // 입력 정규화(트림)
        var normalized = new UserLoginRequest(req.username().trim(), req.password());

        var user = userService.login(normalized);
        if (user == null) {
            model.addAttribute("loginError", "아이디 또는 비밀번호를 확인하세요.");
            return "index";
        }
        session.setAttribute(SESSION_KEY, user); // UserResponse 저장
        return "redirect:/dashboard";
    }

    /* ===================== 비회원(게스트) 로그인 ===================== */
    @PostMapping("/guest/login")
    public String guestLogin(HttpSession session) {
        Object exists = session.getAttribute(SESSION_KEY);
        if (exists instanceof UserResponse) {
            return "redirect:/dashboard";
        }
        String guestName = "게스트-" + (1000 + RND.nextInt(9000));
        session.setAttribute(SESSION_KEY, new GuestSessionUser(guestName));
        return "redirect:/dashboard";
    }

    @GetMapping("/guest/start")
    public String guestStart(HttpSession session) {
        return guestLogin(session);
    }

    /* ===================== 로그아웃 ===================== */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login"; // ✅ 명확한 경로로 변경
    }

    /* ===================== 회원가입 ===================== */
    @GetMapping("/members/new")
    public String signupForm() { return "members/new"; }

    @PostMapping("/members/new")
    public String signup(@Valid UserRequest req, Model model) {
        try {
            // 내부에서 이메일/아이디 정규화 처리함
            userService.register(req);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "members/new";
        }
    }

    /* ===================== 마이페이지 ===================== */
    @GetMapping("/members/mypage")
    public String mypage(HttpSession session, Model model) {
        Object principal = session.getAttribute(SESSION_KEY);
        if (!(principal instanceof UserResponse loginUser)) {
            return "redirect:/login";
        }
        model.addAttribute("user", loginUser);
        return "members/edit-myinfo";
    }

    @PostMapping("/members/mypage")
    public String updateMyinfo(@Valid UserUpdateRequest req, HttpSession session, Model model) {
        Object principal = session.getAttribute(SESSION_KEY);
        if (!(principal instanceof UserResponse loginUser)) {
            return "redirect:/login";
        }
        var updated = userService.updateMyInfo(loginUser.id(), req);
        session.setAttribute(SESSION_KEY, updated);
        model.addAttribute("user", updated);
        model.addAttribute("msg", "저장되었습니다.");
        return "members/edit-myinfo";
    }

    /* ===================== 아이디/비번 찾기 ===================== */
    @GetMapping("/members/find-username")
    public String findUsernamePage() { return "members/find-username"; }

    @PostMapping("/members/find-username")
    public String findUsername(@RequestParam String email, Model model) {
        var username = userService.findUsernameByEmail(email);
        model.addAttribute("email", email);
        model.addAttribute("username", username);
        return "members/find-username";
    }

    @GetMapping("/members/find-password")
    public String findPasswordPage() { return "members/find-password"; }

    @PostMapping("/members/find-password")
    public String resetPassword(@RequestParam String email, Model model) {
        try {
            String temp = userService.resetPasswordToTemp(email);
            model.addAttribute("email", email);
            model.addAttribute("tempPassword", temp);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "members/find-password";
    }
}
