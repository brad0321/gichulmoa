package com.pro.project01.v2.domain.problem.controller;

import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.entity.ProblemType;
import com.pro.project01.v2.domain.problem.service.ProblemService;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import jakarta.servlet.http.HttpSession;
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
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    /**
     * ✅ 문제 목록
     */
    @GetMapping
    public String list(Model model) {
        List<ProblemResponse> problems = problemService.findAll();
        model.addAttribute("problems", problems);
        return "problems/list";
    }

    /**
     * ✅ 문제 등록 폼
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("problem", new ProblemRequest(
                null, null, null, null, null, null, null, null, null, null, null, null
        ));
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", roundRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        model.addAttribute("types", ProblemType.values()); // ✅ 문제 유형 enum 전달
        return "problems/problems-new";
    }

    /**
     * ✅ 문제 등록 처리
     */
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

        // ✅ Enum 변환
        ProblemType type = ProblemType.valueOf(request.type());

        problemService.create(request, imagePath, type);
        return "redirect:/problems";
    }

    /**
     * ✅ 문제 수정 폼
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
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

        String imagePath = null;
        if (!imageFile.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File file = new File(uploadDir + imageFile.getOriginalFilename());
            imageFile.transferTo(file);
            imagePath = imageFile.getOriginalFilename();
        }

        ProblemType type = ProblemType.valueOf(request.type());

        problemService.update(id, request, imagePath, type);
        return "redirect:/problems/" + id;
    }

    /**
     * ✅ 문제 상세
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
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
        problemService.delete(id);
        return "redirect:/problems";
    }

    /**
     * ✅ 문제 풀이 페이지
     */
    @GetMapping("/solve")
    public String getProblemList(Model model, HttpSession session) {
        List<ProblemResponse> problems = problemService.findAll();

        if (problems == null || problems.isEmpty()) {
            model.addAttribute("errorMessage", "등록된 문제가 없습니다.");
        } else {
            model.addAttribute("problems", problems);
        }

        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        return "problems/solve";
    }
}
