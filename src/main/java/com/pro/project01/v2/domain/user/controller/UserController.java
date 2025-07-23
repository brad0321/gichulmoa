package com.pro.project01.v2.domain.user.controller;

import com.pro.project01.v2.domain.user.dto.UserCreateRequest;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import com.pro.project01.v2.domain.user.dto.UserUpdatePasswordRequest;
import com.pro.project01.v2.domain.user.entity.Grade;
import com.pro.project01.v2.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class UserController {

    private final UserService userService;

    // ✅ 회원가입 폼
    @GetMapping("/new")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new UserCreateRequest());
        return "members/new";
    }

    // ✅ 회원가입 처리
    @PostMapping("/new")
    public String processSignUp(@ModelAttribute("user") UserCreateRequest request, HttpServletRequest servletRequest) {
        Long id = userService.register(request);
        UserResponse user = userService.findById(id);

        HttpSession session = servletRequest.getSession(true);
        session.setAttribute("loginUser", user);

        return "redirect:/dashboard";
    }

    // ✅ 마이페이지
    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 게스트 접근 차단
        if (loginUser.grade() == Grade.GUEST) {
            return "redirect:/dashboard?error=guest";
        }

        model.addAttribute("user", loginUser);
        return "members/edit-myinfo";
    }

    // ✅ 비밀번호 수정 처리
    @PostMapping("/mypage")
    public String updatePassword(@ModelAttribute UserUpdatePasswordRequest request, HttpSession session) {
        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        userService.updatePassword(loginUser.id(), request);
        return "redirect:/dashboard";
    }

    // ✅ 회원탈퇴
    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id, HttpSession session) {
        userService.delete(id);
        session.invalidate();
        return "redirect:/";
    }

    // ✅ 아이디 찾기
    @GetMapping("/find-username")
    public String showFindUsernameForm() {
        return "members/find-username";
    }

    @PostMapping("/find-username")
    public String findUsername(@RequestParam String email, Model model) {
        try {
            UserResponse user = userService.findByEmail(email);
            model.addAttribute("email", user.email());
            return "members/show-username";
        } catch (IllegalArgumentException e) {
            model.addAttribute("notFound", true);
            return "members/find-username";
        }
    }

    // ✅ 비밀번호 찾기
    @GetMapping("/find-password")
    public String showFindPasswordForm() {
        return "members/find-password";
    }

    @PostMapping("/find-password")
    public String findPassword(
            @RequestParam String email,
            @RequestParam String name,
            Model model
    ) {
        try {
            UserResponse user = userService.findByEmailAndName(email, name);
            model.addAttribute("user", user);
            return "members/reset-password";
        } catch (IllegalArgumentException e) {
            model.addAttribute("notFound", true);
            return "members/find-password";
        }
    }

    // ✅ 비밀번호 재설정 처리
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam Long id,
                                @RequestParam String password,
                                HttpServletRequest servletRequest) {
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest();
        request.setPassword(password);

        userService.updatePassword(id, request);
        UserResponse user = userService.findById(id);

        servletRequest.getSession(true).setAttribute("loginUser", user);
        return "redirect:/dashboard";
    }
}
