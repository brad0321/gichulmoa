package com.pro.project01.v1.controller;

import com.pro.project01.v1.entity.Member;
import com.pro.project01.v1.service.MemberService;
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

    // ✅ 회원가입 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("member", new Member());
        return "members/new";
    }

    // ✅ 회원가입 처리 + 자동 로그인
    @PostMapping("/new")
    public String create(@ModelAttribute Member member, HttpServletRequest request) {
        memberService.save(member);
        HttpSession session = request.getSession(true);
        session.setAttribute("loginUser", member);
        return "redirect:/dashboard";
    }

    // ✅ 마이페이지 보기
    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        model.addAttribute("member", loginUser);
        return "members/edit-myinfo";
    }

    // ✅ 마이페이지 비밀번호 수정 처리
    @PostMapping("/mypage")
    public String editMyInfo(@RequestParam String password, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        loginUser.setPassword(password);
        memberService.update(loginUser.getId(), loginUser);
        return "redirect:/dashboard";
    }

    // ✅ 회원탈퇴
    @PostMapping("/delete")
    public String deletePost(@RequestParam Long id, HttpSession session) {
        memberService.delete(id);
        session.invalidate();
        return "redirect:/";
    }

    // ✅ 아이디 찾기 폼 (GET)
    @GetMapping("/find-username")
    public String showFindUsernameForm() {
        return "members/find-username";
    }

    // ✅ 아이디 찾기 처리 (POST)
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

    // ✅ 비밀번호 찾기 폼 (GET)
    @GetMapping("/find-password")
    public String showFindPasswordForm() {
        return "members/find-password";
    }

    // ✅ 비밀번호 찾기 처리 (POST)
    @PostMapping("/find-password")
    public String findPassword(@RequestParam String username,
                               @RequestParam String email,
                               Model model) {
        Member member = memberService.findByUsernameAndEmail(username, email);
        if (member == null) {
            model.addAttribute("notFound", true);
            return "members/find-password";
        }
        model.addAttribute("member", member);
        return "members/reset-password";
    }

    // ✅ 비밀번호 재설정 처리 (POST) → 자동 로그인 후 대상복귀
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String password,
                                @RequestParam Long id,
                                HttpServletRequest request) {
        Member member = memberService.findById(id);
        member.setPassword(password);
        memberService.update(id, member);

        // 자동 로그인 처리
        HttpSession session = request.getSession(true);
        session.setAttribute("loginUser", member);

        return "redirect:/dashboard";
    }
}
