package com.pro.project01.v2.domain.calculation.service;

import com.pro.project01.v2.domain.calculation.dto.CalculationProblemResponse;
import com.pro.project01.v2.domain.calculation.entity.CalculationProblem;
import com.pro.project01.v2.domain.calculation.repository.CalculationProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculationProblemServiceImpl implements CalculationProblemService {

    private final CalculationProblemRepository calculationProblemRepository;

    @Override
    public CalculationProblemResponse findById(Long id) {
        CalculationProblem cp = calculationProblemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("계산문제 없음"));
        return toResponse(cp);
    }

    @Override
    public List<CalculationProblemResponse> findAll() {
        return calculationProblemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CalculationProblemResponse findByProblemId(Long problemId) {
        CalculationProblem cp = calculationProblemRepository.findByProblemId(problemId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 Problem에 연결된 계산문제 없음"));
        return toResponse(cp);
    }

    @Override
    public void delete(Long id) {
        calculationProblemRepository.deleteById(id);
    }

    private CalculationProblemResponse toResponse(CalculationProblem cp) {
        return new CalculationProblemResponse(
                cp.getId(),
                cp.getProblem().getId(),
                cp.getCreatedAt(),
                cp.getUpdatedAt()
        );
    }
}
