package com.pro.project01.v2.domain.problemkeyword.controller;

import com.pro.project01.v2.domain.problemkeyword.dto.ProblemKeywordCreateRequest;
import com.pro.project01.v2.domain.problemkeyword.dto.ProblemKeywordResponse;
import com.pro.project01.v2.domain.problemkeyword.service.ProblemKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/problem-keywords")
public class ProblemKeywordController {

    private final ProblemKeywordService keywordService;

    // ✅ 문제에 키워드 추가
    @PostMapping
    public void create(@RequestBody ProblemKeywordCreateRequest request) {
        keywordService.create(request);
    }

    // ✅ 문제 ID로 키워드 조회
    @GetMapping("/{problemId}")
    public List<ProblemKeywordResponse> findByProblemId(@PathVariable Long problemId) {
        return keywordService.findByProblemId(problemId);
    }

    // ✅ 문제 삭제 시 키워드 일괄 삭제 (관리자용)
    @DeleteMapping("/{problemId}")
    public void deleteByProblemId(@PathVariable Long problemId) {
        keywordService.deleteByProblemId(problemId);
    }
}
