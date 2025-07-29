package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.unit.entity.Unit;
import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    @Override
    public ProblemResponse create(ProblemRequest request, String imagePath) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Round round = roundRepository.findById(request.roundId())
                .orElseThrow(() -> new RuntimeException("Round not found"));
        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        // ✅ Builder 사용
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
                .subject(subject)
                .round(round)
                .unit(unit)
                .build();

        return ProblemResponse.fromEntity(problemRepository.save(problem));
    }

    @Override
    public List<ProblemResponse> findAll() {
        return problemRepository.findAll().stream()
                .map(ProblemResponse::fromEntity)
                .toList();
    }

    @Override
    public ProblemResponse findById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        return ProblemResponse.fromEntity(problem);
    }

    @Override
    public void delete(Long id) {
        problemRepository.deleteById(id);
    }
}
