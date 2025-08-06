package com.pro.project01.v2.domain.problem.controller;

import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.dto.ProblemResponseForSolve;
import com.pro.project01.v2.domain.problem.entity.ProblemType;

import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.problem.service.ProblemService;
import com.pro.project01.v2.domain.round.dto.RoundDto;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.unit.dto.UnitDto;

import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    /**
     * ✅ 문제 목록
     */
    @GetMapping
    public String list(Model model) {
        List<ProblemResponse> problems = problemService.findAll();
        log.info("[GET] 문제 목록 요청, size={}", problems.size());
        model.addAttribute("problems", problems);
        return "problems/list";
    }

    /**
     * ✅ 문제 등록 폼
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        log.info("[GET] 문제 등록 폼 요청");
        model.addAttribute("problem", new ProblemRequest(
                null, null, null, null, null, null, null, null, null, null, null, null
        ));
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", roundRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        model.addAttribute("types", ProblemType.values());
        return "problems/problems-new";
    }

    /**
     * ✅ 문제 등록 처리
     */
    @PostMapping("/new")
    public String create(@ModelAttribute ProblemRequest request,
                         @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        log.info("[POST] 문제 등록 요청: {}", request);

        String imagePath = null;
        if (!imageFile.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File file = new File(uploadDir + imageFile.getOriginalFilename());
            imageFile.transferTo(file);
            imagePath = imageFile.getOriginalFilename();
            log.info("이미지 업로드 완료: {}", imagePath);
        }

        ProblemType type = ProblemType.valueOf(request.type());
        problemService.create(request, imagePath, type);
        log.info("문제 등록 완료");

        return "redirect:/problems";
    }

    /**
     * ✅ 문제 수정 폼
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("[GET] 문제 수정 폼 요청: id={}", id);
        ProblemResponse problem = problemService.findById(id);
        model.addAttribute("problem", problem);
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", roundRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        model.addAttribute("types", ProblemType.values());
        return "problems/edit";
    }

    /**
     * ✅ 문제 수정 처리
     */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute ProblemRequest request,
                         @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        log.info("[POST] 문제 수정 요청: id={}, data={}", id, request);

        String imagePath = null;
        if (!imageFile.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File file = new File(uploadDir + imageFile.getOriginalFilename());
            imageFile.transferTo(file);
            imagePath = imageFile.getOriginalFilename();
            log.info("이미지 업로드 완료: {}", imagePath);
        }

        ProblemType type = ProblemType.valueOf(request.type());
        problemService.update(id, request, imagePath, type);
        log.info("문제 수정 완료: id={}", id);
        return "redirect:/problems/" + id;
    }

    /**
     * ✅ 문제 상세
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        log.info("[GET] 문제 상세 요청: id={}", id);
        ProblemResponse problem = problemService.findById(id);
        model.addAttribute("problem", problem);

        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        return "problems/detail";
    }

    /**
     * ✅ 문제 삭제
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        log.info("[POST] 문제 삭제 요청: id={}", id);
        problemService.delete(id);
        return "redirect:/problems";
    }

    // 문제 풀이 페이지
    @GetMapping("/solve")
    public String solvePage() {
        log.info("[GET] 문제 풀이 페이지 요청");
        return "problems/solve";
    }

    // ✅ 과목 리스트 API
    @ResponseBody
    @GetMapping("/api/subjects")
    public List<Subject> getSubjects() {
        log.info("[API] 과목 리스트 요청");
        return subjectRepository.findAll();
    }


    // ✅ 회차 리스트 API
    @ResponseBody
    @GetMapping("/api/rounds")
    public List<RoundDto> getRounds(@RequestParam("subjectId") Long subjectId)
    {
        log.info("[API] 회차 리스트 요청: subjectId={}", subjectId);
        return roundRepository.findBySubject_Id(subjectId)
                .stream()
                .map(r -> new RoundDto(r.getId(), r.getRoundNumber(), r.getName()))
                .toList();
    }

    // ✅ 목차 리스트 API
    @ResponseBody
    @GetMapping("/api/units")
    public List<UnitDto> getUnits(@RequestParam("subjectId") Long subjectId)
    {
        log.info("[API] 목차 리스트 요청: subjectId={}", subjectId);
        return unitRepository.findBySubject_Id(subjectId)
                .stream()
                .map(u -> new UnitDto(u.getId(), u.getName()))
                .toList();
    }

    // ✅ 문제 리스트 API
    @ResponseBody
    @GetMapping("/api/problems")
    public List<ProblemResponseForSolve> getProblems(
            @RequestParam Long subjectId,
            @RequestParam(required = false) List<Long> roundIds,
            @RequestParam(required = false) Long unitId
    ) {
        log.info("[API] 문제 리스트 요청: subjectId={}, roundIds={}, unitId={}",
                subjectId, roundIds, unitId);

        return problemRepository.findByFilters(subjectId, roundIds, unitId)
                .stream()
                .map(problem -> new ProblemResponseForSolve(
                        problem.getId(),
                        problem.getTitle(),
                        problem.getViewContent(),
                        problem.getImageUrl(),
                        List.of(
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice1()),
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice2()),
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice3()),
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice4()),
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice5())
                        )
                ))
                .toList();
    }

}
