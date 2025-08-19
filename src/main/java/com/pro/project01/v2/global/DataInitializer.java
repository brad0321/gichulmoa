package com.pro.project01.v2.global;

import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.unit.entity.Unit;
import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import com.pro.project01.v2.domain.user.entity.Grade;
import com.pro.project01.v2.domain.user.entity.Role;
import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

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
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    @Override
    public void run(String... args) {

        Subject subject1 = addSubjectIfMissing("부동산학개론");
        Subject subject2 = addSubjectIfMissing("민법");
        addRoundsIfMissing(subject1);
        addRoundsIfMissing(subject2);
        addUnitsIfMissing(subject1);
        addUnitsIfMissing(subject2);
        addAdminIfMissing("brad0321", "36933693");
    }

        private Subject addSubjectIfMissing(String name) {
            return subjectRepository.findByName(name)
                    .orElseGet(() -> subjectRepository.save(Subject.builder().name(name).build()));
        }

        private void addRoundsIfMissing(Subject subject) {
            if (!roundRepository.findBySubject_Id(subject.getId()).isEmpty()) {
                return;
            }
            for (int i = 26; i <= 35; i++) {
                roundRepository.save(Round.builder()
                        .roundNumber(i)
                        .name(i + "회차")
                        .subject(subject)
                        .build());
            }
        }

        private void addUnitsIfMissing(Subject subject) {
            if (!unitRepository.findBySubject_Id(subject.getId()).isEmpty()) {
                return;
            }
            var names = List.of(
                    "부동산의 개념과 분류",
                    "부동산의 특성(속성)",
                    "부동산 경제론",
                    "부동산 시장론",
                    "부동산입지론과 공간구조론",
                    "부동산 정책론",
                    "부동산 투자론",
                    "부동산 금융론",
                    "부동산 개발론",
                    "부동산 관리론",
                    "부동산 마케팅론",
                    "감정평가의 기초이론",
                    "감정평가 3방식 7방법",
                    "부동산가격 공시제도"
            );
            for (String name : names) {
                unitRepository.save(Unit.builder()
                        .name(name)
                        .subject(subject)
                        .build());
            }
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