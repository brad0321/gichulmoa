package com.pro.project01.service;

import com.pro.project01.entity.*;
import com.pro.project01.repository.ProblemRepository;
import com.pro.project01.repository.RoundRepository;
import com.pro.project01.repository.SubjectRepository;
import com.pro.project01.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    // 폼 초기화용
    public ProblemForm createForm() {
        return new ProblemForm();
    }

    // 과목 목록
    public List<Subject> getSubjects() {
        return subjectRepository.findAll();
    }

    // 회차 목록
    public List<Round> getRounds() {
        return roundRepository.findAll();
    }

    // 목차 목록
    public List<Unit> getUnits() {
        return unitRepository.findAll();
    }

    // 문제 등록
    public Problem saveProblem(ProblemForm form) throws IOException {
        String fileName = null;
        MultipartFile viewImage = form.getViewImage();

        if (viewImage != null && !viewImage.isEmpty()) {
            fileName = UUID.randomUUID() + "_" + viewImage.getOriginalFilename();
            String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File uploadFile = new File(uploadDir, fileName);
            viewImage.transferTo(uploadFile);
        }

        Subject subject = subjectRepository.findById(form.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("과목이 존재하지 않습니다."));
        Round round = roundRepository.findById(form.getRoundId())
                .orElseThrow(() -> new IllegalArgumentException("회차가 존재하지 않습니다."));
        Unit unit = unitRepository.findById(form.getUnitId())
                .orElseThrow(() -> new IllegalArgumentException("목차가 존재하지 않습니다."));

        Problem problem = Problem.builder()
                .title(form.getTitle())
                .viewContent(form.getViewContent())
                .viewImagePath(fileName)
                .choice1(form.getChoice1())
                .choice2(form.getChoice2())
                .choice3(form.getChoice3())
                .choice4(form.getChoice4())
                .choice5(form.getChoice5())
                .subject(subject)
                .round(round)
                .unit(unit)
                .build();

        return problemRepository.save(problem);
    }

    // 문제 전체 조회
    public List<Problem> findAllProblems() {
        return problemRepository.findAll();
    }

    // 문제 단건 조회
    public Problem findProblemById(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));
    }

    // 문제 삭제
    public void deleteProblem(Long id) {
        problemRepository.deleteById(id);
    }

    // ✅ 문제 수정
    public Problem updateProblem(Long id, ProblemForm form) throws IOException {
        Problem problem = findProblemById(id);

        // 기존 이미지 유지 or 새 이미지 업로드
        String fileName = problem.getViewImagePath();
        MultipartFile viewImage = form.getViewImage();
        if (viewImage != null && !viewImage.isEmpty()) {
            fileName = UUID.randomUUID() + "_" + viewImage.getOriginalFilename();
            String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File uploadFile = new File(uploadDir, fileName);
            viewImage.transferTo(uploadFile);
        }

        // 연관 객체
        Subject subject = subjectRepository.findById(form.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("과목이 존재하지 않습니다."));
        Round round = roundRepository.findById(form.getRoundId())
                .orElseThrow(() -> new IllegalArgumentException("회차가 존재하지 않습니다."));
        Unit unit = unitRepository.findById(form.getUnitId())
                .orElseThrow(() -> new IllegalArgumentException("목차가 존재하지 않습니다."));

        // 값 수정
        problem.setTitle(form.getTitle());
        problem.setViewContent(form.getViewContent());
        problem.setViewImagePath(fileName);
        problem.setChoice1(form.getChoice1());
        problem.setChoice2(form.getChoice2());
        problem.setChoice3(form.getChoice3());
        problem.setChoice4(form.getChoice4());
        problem.setChoice5(form.getChoice5());
        problem.setSubject(subject);
        problem.setRound(round);
        problem.setUnit(unit);

        return problemRepository.save(problem);
    }
}
