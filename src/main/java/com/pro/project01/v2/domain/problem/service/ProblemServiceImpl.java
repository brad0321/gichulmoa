package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.ProblemCreateRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.dto.ProblemUpdateRequest;
import com.pro.project01.v2.domain.problem.entity.*;
import com.pro.project01.v2.domain.problem.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    private final String uploadDir = "src/main/resources/static/uploads";

    @Override
    public Long create(ProblemCreateRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("과목이 존재하지 않습니다."));
        Round round = roundRepository.findById(request.getRoundId())
                .orElseThrow(() -> new IllegalArgumentException("회차가 존재하지 않습니다."));
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new IllegalArgumentException("목차가 존재하지 않습니다."));

        String imageUrl = null;
        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = trySaveFile(imageFile);
        }

        Problem problem = Problem.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(imageUrl)
                .viewContent(request.getViewContent())
                .choice1(request.getChoice1())
                .choice2(request.getChoice2())
                .choice3(request.getChoice3())
                .choice4(request.getChoice4())
                .choice5(request.getChoice5())
                .subject(subject)
                .round(round)
                .unit(unit)
                .build();

        problemRepository.save(problem);
        return problem.getId();
    }

    @Override
    public void update(Long id, ProblemUpdateRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("과목이 존재하지 않습니다."));
        Round round = roundRepository.findById(request.getRoundId())
                .orElseThrow(() -> new IllegalArgumentException("회차가 존재하지 않습니다."));
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new IllegalArgumentException("목차가 존재하지 않습니다."));

        String imageUrl = problem.getImageUrl();
        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = trySaveFile(imageFile);
        }

        problem.update(
                request.getTitle(),
                request.getContent(),
                imageUrl,
                request.getViewContent(),
                request.getChoice1(),
                request.getChoice2(),
                request.getChoice3(),
                request.getChoice4(),
                request.getChoice5(),
                subject,
                round,
                unit
        );
    }

    @Override
    public ProblemResponse findById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제가 없습니다."));
        return ProblemResponse.from(problem);
    }

    @Override
    public List<ProblemResponse> findAll() {
        return problemRepository.findAll().stream()
                .map(ProblemResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        problemRepository.deleteById(id);
    }

    private String trySaveFile(MultipartFile file) {
        try {
            return saveFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String newFilename = uuid + "_" + originalFilename;

        String uploadPath = new File(uploadDir).getAbsolutePath();
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(uploadPath, newFilename);
        file.transferTo(dest);

        return "/uploads/" + newFilename;
    }
}