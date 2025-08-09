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

    /* ===================== 회원 로그인 ===================== */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value="error", required=false) String error, Model model) {
        if (error != null) model.addAttribute("loginError", "아이디 또는 비밀번호를 확인하세요.");
        return "index";
    }

    @PostMapping("/login")
    public String doLogin(@Valid UserLoginRequest req, HttpSession session, Model model) {
        var user = userService.login(req);
        if (user == null) {
            model.addAttribute("loginError", "아이디 또는 비밀번호를 확인하세요.");
            return "index";
        }
        session.setAttribute("loginUser", user); // UserResponse
        return "redirect:/dashboard";
    }

    /* ===================== 비회원(게스트) 임시 로그인 ===================== */
    @PostMapping("/guest/login")
    public String guestLogin(HttpSession session) {
        // 이미 회원으로 로그인 중이면 그대로 대시보드
        Object exists = session.getAttribute("loginUser");
        if (exists instanceof UserResponse) {
            return "redirect:/dashboard";
        }
        // 게스트 이름 랜덤
        String guestName = "게스트-" + (1000 + RND.nextInt(9000));
        session.setAttribute("loginUser", new GuestSessionUser(guestName));
        return "redirect:/dashboard";
    }

    // (선택) GET 진입도 허용하고 싶으면 버튼에서 이걸 사용
    @GetMapping("/guest/start")
    public String guestStart(HttpSession session) {
        return guestLogin(session);
    }

    /* ===================== 로그아웃 ===================== */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index";
    }

    /* ===================== 회원가입 ===================== */
    @GetMapping("/members/new")
    public String signupForm() { return "members/new"; }

    @PostMapping("/members/new")
    public String signup(@Valid UserRequest req, Model model) {
        try {
            userService.register(req);
            return "redirect:/index";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "members/new";
        }
    }

    /* ===================== 마이페이지 ===================== */
    @GetMapping("/members/mypage")
    public String mypage(HttpSession session, Model model) {
        Object principal = session.getAttribute("loginUser");
        // 회원만 접근 허용 (게스트 or 비로그인 → 로그인 페이지)
        if (!(principal instanceof UserResponse loginUser)) {
            return "redirect:/index";
        }
        model.addAttribute("user", loginUser);
        return "edit-myinfo";
    }

    @PostMapping("/members/mypage")
    public String updateMyinfo(@Valid UserUpdateRequest req, HttpSession session, Model model) {
        Object principal = session.getAttribute("loginUser");
        if (!(principal instanceof UserResponse loginUser)) {
            return "redirect:/login";
        }
        var updated = userService.updateMyInfo(loginUser.id(), req);
        session.setAttribute("loginUser", updated);
        model.addAttribute("user", updated);
        model.addAttribute("msg", "저장되었습니다.");
        return "edit-myinfo";
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
