package com.pro.project01.service;

import com.pro.project01.entity.Member;
import com.pro.project01.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
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
        return memberRepository.findByUsernameAndEmail(username, email)
                .orElse(null);
    }

}
