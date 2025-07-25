package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.ProblemCreateRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.dto.ProblemUpdateRequest;
import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.entity.Round;
import com.pro.project01.v2.domain.problem.entity.Subject;
import com.pro.project01.v2.domain.problem.entity.Unit;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.problem.repository.RoundRepository;
import com.pro.project01.v2.domain.problem.repository.SubjectRepository;
import com.pro.project01.v2.domain.problem.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    @Override
    public Long create(ProblemCreateRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("과목 없음"));
        Round round = roundRepository.findById(request.roundId())
                .orElseThrow(() -> new IllegalArgumentException("회차 없음"));
        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new IllegalArgumentException("목차 없음"));

        Problem problem = Problem.builder()
                .title(request.title())
                .viewContent(request.viewContent())
                .choice1(request.choice1())
                .choice2(request.choice2())
                .choice3(request.choice3())
                .choice4(request.choice4())
                .choice5(request.choice5())
                .subject(subject)
                .round(round)
                .unit(unit)
                .build();

        problemRepository.save(problem);
        return problem.getId();
    }

    @Override
    public List<ProblemResponse> findAll() {
        return problemRepository.findAll().stream()
                .map(ProblemResponse::from)
                .toList();
    }

    @Override
    public ProblemResponse findById(Long id) {
        return ProblemResponse.from(problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제 없음")));
    }

    @Override
    public void update(Long id, ProblemUpdateRequest request, MultipartFile viewImage) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("과목 없음"));
        Round round = roundRepository.findById(request.roundId())
                .orElseThrow(() -> new IllegalArgumentException("회차 없음"));
        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new IllegalArgumentException("목차 없음"));

        // ✅ 엔티티 업데이트
        problem.update(
                request.title(),
                request.viewContent(),
                request.choice1(),
                request.choice2(),
                request.choice3(),
                request.choice4(),
                request.choice5(),
                subject, round, unit
        );

        // ✅ 이미지 업로드 처리
        if (viewImage != null && !viewImage.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + viewImage.getOriginalFilename();
            Path filePath = Paths.get("src/main/resources/static/uploads/" + fileName);

            try {
                Files.createDirectories(filePath.getParent());
                viewImage.transferTo(filePath.toFile());
                problem.updateImagePath(fileName);
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }

        problemRepository.save(problem);
    }

    @Override
    public void delete(Long id) {
        problemRepository.deleteById(id);
    }
}
