package com.pro.project01.controller;

import com.pro.project01.entity.Member;
import com.pro.project01.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberRepository memberRepository;

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session
    ) {
        Member member = memberRepository.findByUsername(username)
                .orElse(null);

        if (member == null || !member.getPassword().equals(password)) {
            return "redirect:/?error=true";
        }

        // 로그인 성공 시 Session에 저장
        session.setAttribute("loginUser", member);

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
