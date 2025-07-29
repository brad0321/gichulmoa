package com.pro.project01.v2.domain.problem.controller;

import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.service.ProblemService;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final SubjectRepository subjectRepository;  // ✅ 추가
    private final RoundRepository roundRepository;      // ✅ 추가
    private final UnitRepository unitRepository;        // ✅ 추가

    // ✅ 문제 목록
    @GetMapping
    public String list(Model model) {
        List<ProblemResponse> problems = problemService.findAll();
        model.addAttribute("problems", problems);
        return "problems/list";
    }

    // ✅ 문제 등록 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("problem", new ProblemRequest(
                null, null, null, null, null, null, null, null, null, null, null
        ));
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", roundRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "problems/problems-new";
    }

    // ✅ 문제 등록 처리
    @PostMapping("/new")
    public String create(@ModelAttribute ProblemRequest request,
                         @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        String imagePath = null;
        if (!imageFile.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File file = new File(uploadDir + imageFile.getOriginalFilename());
            imageFile.transferTo(file);
            imagePath = imageFile.getOriginalFilename();
        }

        problemService.create(request, imagePath);
        return "redirect:/problems";
    }
}
