package com.pro.project01.v2.global;

import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.user.entity.Grade;
import com.pro.project01.v2.domain.user.entity.Role;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 기본 과목 데이터를 주입한다.
 * "부동산학개론", "민법" 과목이 없으면 추가한다.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        addSubjectIfMissing("부동산학개론");
        addSubjectIfMissing("민법");
        addAdminIfMissing("brad0321", "36933693");
    }

    private void addSubjectIfMissing(String name) {
        subjectRepository.findByName(name)
                .orElseGet(() -> subjectRepository.save(Subject.builder().name(name).build()));
    }

    private void addAdminIfMissing(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User admin = User.builder()
                .username(username)
                .email(username + "@example.com")
                .password(passwordEncoder.encode(password))
                .grade(Grade.GOLD)
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
    }
}