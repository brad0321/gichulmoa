package com.pro.project01.controller;

import com.pro.project01.entity.Problem;
import com.pro.project01.repository.ProblemRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemRepository problemRepository;

    @GetMapping("/problems/new")
    public String createProblemForm() {
        return "problems/problem-new";
    }

    @PostMapping("/problems/new")
    public String saveProblem(
            @RequestParam String title,
            @RequestParam(required = false) String viewContent,
            @RequestParam(required = false) MultipartFile viewImage,
            @RequestParam(required = false) String choice1,
            @RequestParam(required = false) String choice2,
            @RequestParam(required = false) String choice3,
            @RequestParam(required = false) String choice4,
            @RequestParam(required = false) String choice5
    ) throws IOException {

        String fileName = null;

        if (viewImage != null && !viewImage.isEmpty()) {
            fileName = UUID.randomUUID() + "_" + viewImage.getOriginalFilename();

            String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File uploadFile = new File(uploadDir, fileName);
            viewImage.transferTo(uploadFile);
        }

        Problem problem = Problem.builder()
                .title(title)
                .viewContent(viewContent)
                .viewImagePath(fileName)
                .choice1(choice1)
                .choice2(choice2)
                .choice3(choice3)
                .choice4(choice4)
                .choice5(choice5)
                .build();

        Problem savedProblem = problemRepository.save(problem);

        if (savedProblem == null || savedProblem.getId() == null) {
            return "redirect:/problems";
        }

        return "redirect:/problems/" + savedProblem.getId();
    }

    @GetMapping("/problems")
    public String listProblems(Model model) {
        List<Problem> problems = problemRepository.findAll();
        model.addAttribute("problems", problems);
        return "problems/list";
    }

    @GetMapping("/problems/{id}")
    public String detailProblem(@PathVariable Long id, Model model, HttpSession session) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));
        model.addAttribute("problem", problem);

        // ✅ 관리자 버튼용 loginUser 전달
        model.addAttribute("loginUser", session.getAttribute("loginUser"));

        return "problems/detail";
    }

    @GetMapping("/problems/{id}/edit")
    public String editProblemForm(@PathVariable Long id, Model model) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));
        model.addAttribute("problem", problem);
        return "problems/edit";
    }

    @PostMapping("/problems/{id}/edit")
    public String editProblem(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String viewContent,
            @RequestParam(required = false) MultipartFile viewImage,
            @RequestParam(required = false) String choice1,
            @RequestParam(required = false) String choice2,
            @RequestParam(required = false) String choice3,
            @RequestParam(required = false) String choice4,
            @RequestParam(required = false) String choice5,
            HttpServletRequest request
    ) throws IOException {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));

        problem.setTitle(title);
        problem.setViewContent(viewContent);
        problem.setChoice1(choice1);
        problem.setChoice2(choice2);
        problem.setChoice3(choice3);
        problem.setChoice4(choice4);
        problem.setChoice5(choice5);

        if (viewImage != null && !viewImage.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + viewImage.getOriginalFilename();

            String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File uploadFile = new File(uploadDir, fileName);
            viewImage.transferTo(uploadFile);

            problem.setViewImagePath(fileName);
        }

        problemRepository.save(problem);
        return "redirect:/problems/" + problem.getId();
    }

    @PostMapping("/problems/{id}/delete")
    public String deleteProblem(@PathVariable Long id) {
        problemRepository.deleteById(id);
        return "redirect:/problems";
    }

    @GetMapping("/solve")
    public String solveProblems(Model model) {
        List<Problem> problems = problemRepository.findAll();
        model.addAttribute("problems", problems);
        return "problems/solve";
    }
}
