package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pro.project01.v2.domain.calculation.repository.CalculationProblemRepository; // ★추가


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;
    private final CalculationProblemRepository calculationProblemRepository;  // ★추가


    @Override
    public List<ProblemResponse> findAll() {
        return problemRepository.findAll().stream()
                .map(ProblemResponse::fromEntity)
                .toList();
    }

    @Override
    public ProblemResponse findById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));
        return ProblemResponse.fromEntity(problem);
    }

    @Override
    public Long create(ProblemRequest request, String imagePath) {
        Problem problem = Problem.builder()
                .title(request.title())
                .viewContent(request.viewContent())
                .imageUrl(imagePath)
                .choice1(request.choice1())
                .choice2(request.choice2())
                .choice3(request.choice3())
                .choice4(request.choice4())
                .choice5(request.choice5())
                .answer(request.answer())
                .subject(subjectRepository.findById(request.subjectId())
                        .orElseThrow(() -> new IllegalArgumentException("과목을 찾을 수 없습니다.")))
                .round(roundRepository.findById(request.roundId())
                        .orElseThrow(() -> new IllegalArgumentException("회차를 찾을 수 없습니다.")))
                .unit(unitRepository.findById(request.unitId())
                        .orElseThrow(() -> new IllegalArgumentException("목차를 찾을 수 없습니다.")))
                .build();

        problemRepository.save(problem);

        return problem.getId();
    }


    @Override
    public void update(Long id, ProblemRequest request, String imagePath) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));

        problem.update(
                request.title(),
                request.viewContent(),
                imagePath,
                request.choice1(),
                request.choice2(),
                request.choice3(),
                request.choice4(),
                request.choice5(),
                request.answer(),
                subjectRepository.findById(request.subjectId())
                        .orElseThrow(() -> new IllegalArgumentException("과목을 찾을 수 없습니다.")),
                roundRepository.findById(request.roundId())
                        .orElseThrow(() -> new IllegalArgumentException("회차를 찾을 수 없습니다.")),
                unitRepository.findById(request.unitId())
                        .orElseThrow(() -> new IllegalArgumentException("목차를 찾을 수 없습니다."))
        );
    }

    @Override
    public void delete(Long id) {
        problemRepository.deleteById(id);
    }
}
