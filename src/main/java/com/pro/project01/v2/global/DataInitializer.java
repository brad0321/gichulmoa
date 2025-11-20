package com.pro.project01.v2.global;

import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 과목 + 코드(SS)
        Subject subject1 = addSubjectIfMissing("부동산학개론", "01");
        Subject subject2 = addSubjectIfMissing("민법",       "02");

        // 회차(26~35) 생성
        addRoundsIfMissing(subject1, 26, 35);
        addRoundsIfMissing(subject2, 26, 35);

        // 단원 생성 (seqCode=001.., 각 과목의 최신 회차에 연결)
        addUnitsIfMissing(subject1);
        addUnitsIfMissing(subject2);

        // 관리자 계정
        addAdminIfMissing("brad0321", "36933693");
    }

    /** 과목이 없으면 (name, code)로 생성 */
    private Subject addSubjectIfMissing(String name, String code) {
        // 우선 code로 찾고, 없으면 name으로도 찾아봄(이전 데이터 호환)
        Optional<Subject> byCode = findByCodeSafe(code);
        if (byCode.isPresent()) return byCode.get();

        return subjectRepository.findByName(name)
                .orElseGet(() -> subjectRepository.save(
                        Subject.builder()
                                .name(name)
                                .code(code)
                                .build()
                ));
    }

    // SubjectRepository에 findByCode가 없다면, 아래처럼 name으로만 찾도록 바꾸거나,
    // 커스텀 메서드를 추가하세요.
    private Optional<Subject> findByCodeSafe(String code) {
        try {
            // subjectRepository.findByCode(code) 가 존재한다면 사용
            var method = subjectRepository.getClass().getMethod("findByCode", String.class);
            Object result = method.invoke(subjectRepository, code);
            @SuppressWarnings("unchecked")
            Optional<Subject> opt = (Optional<Subject>) result;
            return opt;
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    /** 과목별 회차(26~to)까지 없으면 생성 */
    private void addRoundsIfMissing(Subject subject, int from, int to) {
        // 기존 회차 조회
        List<Round> existing = roundRepository.findBySubject_Id(subject.getId());
        // 필요한 범위 채움
        for (int i = from; i <= to; i++) {
            final int rr = i;
            boolean exists = existing.stream().anyMatch(r -> r.getRoundNumber() != null && r.getRoundNumber() == rr);
            if (!exists) {
                roundRepository.save(
                        Round.builder()
                                .roundNumber((short) rr)
                                .name(rr + "회차")
                                .subject(subject)
                                .build()
                );
            }
        }
    }

    /** 단원 기본 세트(이름 리스트) 없으면 생성, seqCode = 001.., round = 최신회차에 연결 */
    private void addUnitsIfMissing(Subject subject) {
        if (!unitRepository.findBySubject_Id(subject.getId()).isEmpty()) {
            return; // 이미 존재
        }

        // 해당 과목 최신 회차 찾기(가장 큰 number)
        Round latestRound = roundRepository.findBySubject_Id(subject.getId()).stream()
                .filter(r -> r.getRoundNumber() != null)
                .max(Comparator.comparing(Round::getRoundNumber))
                .orElse(null);

        // 최신 회차가 없다면 생성 로직을 먼저 실행해야 함
        if (latestRound == null) {
            addRoundsIfMissing(subject, 26, 35);
            latestRound = roundRepository.findBySubject_Id(subject.getId()).stream()
                    .filter(r -> r.getRoundNumber() != null)
                    .max(Comparator.comparing(Round::getRoundNumber))
                    .orElseThrow(() -> new IllegalStateException("회차 데이터가 없습니다: subject=" + subject.getName()));
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

        for (int i = 0; i < names.size(); i++) {
            String seq = lpad3(i + 1); // 001, 002, ...
            unitRepository.save(
                    Unit.builder()
                            .name(names.get(i))
                            .seqCode(seq)          // UUU
                            .subject(subject)
                            .round(latestRound)    // FK not null
                            .build()
            );
        }
    }

    private String lpad3(int n) {
        if (n < 10)  return "00" + n;
        if (n < 100) return "0"  + n;
        return String.valueOf(n);
    }

    private void addAdminIfMissing(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User admin = User.builder()
                .username(username)
                .email(username + "@example.com")
                .password(passwordEncoder.encode(rawPassword))
                .grade(Grade.GOLD)
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
    }
}
