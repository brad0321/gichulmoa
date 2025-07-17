package com.pro.project01.controller;

import com.pro.project01.entity.Member;
import com.pro.project01.entity.Role;
import com.pro.project01.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("member", new Member());
        return "members/new";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Member member, HttpServletRequest request) {
        // ✅ 0) 기본 권한 세팅
        member.setRole(Role.USER);

        // 1) 회원 저장
        memberService.save(member);

        // 2) 자동로그인
        HttpSession session = request.getSession(true);
        session.setAttribute("loginUser", member);

        // 3) 대시보드로 이동
        return "redirect:/dashboard";
    }

    @GetMapping("/find-username")
    public String findUsernameForm() {
        return "members/find-username";
    }

    @PostMapping("/find-username")
    public String findUsername(@RequestParam String email, Model model) {
        Member member = memberService.findByEmail(email);

        if (member == null) {
            model.addAttribute("notFound", true);
            return "members/find-username";
        }

        model.addAttribute("username", member.getUsername());
        return "members/show-username";
    }

    @GetMapping("/find-password")
    public String findPasswordForm() {
        return "members/find-password";
    }

    @PostMapping("/find-password")
    public String findPassword(
            @RequestParam String username,
            @RequestParam String email,
            HttpSession session,
            Model model
    ) {
        Member member = memberService.findByUsernameAndEmail(username, email);

        if (member == null) {
            model.addAttribute("notFound", true);
            return "members/find-password";
        }

        // 세션에 비밀번호 변경 대상 사용자 저장
        session.setAttribute("resetUser", member);
        return "redirect:/members/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("resetUser");

        if (member == null) {
            return "redirect:/";
        }

        model.addAttribute("member", member);
        return "members/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String password,
            HttpSession session
    ) {
        Member member = (Member) session.getAttribute("resetUser");

        if (member == null) {
            return "redirect:/";
        }

        member.setPassword(password);
        memberService.save(member);

        // 비밀번호 찾기 완료 후 자동 로그인
        session.setAttribute("loginUser", member);
        session.removeAttribute("resetUser");

        return "redirect:/dashboard";
    }

    @GetMapping("/me/edit")
    public String editMyInfoForm(HttpSession session, Model model) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/";
        }

        model.addAttribute("member", loginUser);
        return "members/edit-myinfo";
    }

    @PostMapping("/me/edit")
    public String editMyInfo(
            @RequestParam String password,
            HttpSession session
    ) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/";
        }

        // 비밀번호만 수정
        loginUser.setPassword(password);
        memberService.save(loginUser);

        // 세션 업데이트
        session.setAttribute("loginUser", loginUser);

        return "redirect:/dashboard";
    }
}
