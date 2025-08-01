package com.pro.project01.v1.service;

import com.pro.project01.v1.entity.Member;
import com.pro.project01.v1.entity.Role;
import com.pro.project01.v1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member save(Member member) {
        if (member.getRole() == null) {
            member.setRole(Role.USER); // 기본 권한 설정
        }
        return memberRepository.save(member);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    public Member update(Long id, Member updateData) {
        Member member = findById(id);
        member.setUsername(updateData.getUsername());
        member.setPassword(updateData.getPassword());
        member.setEmail(updateData.getEmail());
        return memberRepository.save(member);
    }

    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }

    public Member findByUsernameAndEmail(String username, String email) {
        return memberRepository.findByUsernameAndEmail(username, email).orElse(null);
    }
}
