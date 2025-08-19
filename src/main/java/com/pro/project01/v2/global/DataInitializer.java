package com.pro.project01.v2.global;

import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * 애플리케이션 시작 시 기본 과목 데이터를 주입한다.
 * "부동산학개론", "민법" 과목이 없으면 추가한다.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SubjectRepository subjectRepository;

    @Override
    public void run(String... args) {
        addSubjectIfMissing("부동산학개론");
        addSubjectIfMissing("민법");
    }

    private void addSubjectIfMissing(String name) {
        subjectRepository.findByName(name)
                .orElseGet(() -> subjectRepository.save(Subject.builder().name(name).build()));
    }
}