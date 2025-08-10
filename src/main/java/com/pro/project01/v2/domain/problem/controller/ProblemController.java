package com.pro.project01.v2.domain.problem.controller;

import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.dto.ProblemResponseForSolve;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.problem.service.ProblemService;
import com.pro.project01.v2.domain.round.dto.RoundDto;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.unit.dto.UnitDto;
import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    /** ✅ 문제 목록 */
    @GetMapping
    public String list(Model model, HttpSession session) {
        List<ProblemResponse> problems = problemService.findAll();
        log.info("[GET] 문제 목록 요청, size={}", problems.size());
        model.addAttribute("problems", problems);

        // 관리자 버튼 노출용
        Object principal = session.getAttribute("loginUser");
        if (principal instanceof UserResponse user) {
            model.addAttribute("loginUser", user);
        }
        return "problems/list";
    }

    /** ✅ 문제 등록 폼 */
    @GetMapping("/new")
    public String createForm(Model model) {
        log.info("[GET] 문제 등록 폼 요청");
        model.addAttribute("problem", new ProblemRequest(
                null, null, null, null, null, null, null, null, null, null, null
        ));
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", roundRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "problems/problems-new";
    }

    /** ✅ 문제 등록 처리 */
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

        problemService.create(request, imagePath);
        log.info("문제 등록 완료");
        return "redirect:/problems";
    }

    /** ✅ 문제 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("[GET] 문제 수정 폼 요청: id={}", id);
        ProblemResponse problem = problemService.findById(id);
        model.addAttribute("problem", problem);
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", roundRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "problems/edit";
    }

    /** ✅ 문제 수정 처리 */
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

        problemService.update(id, request, imagePath);
        log.info("문제 수정 완료: id={}", id);
        return "redirect:/problems/" + id;
    }

    /** ✅ 문제 상세 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        log.info("[GET] 문제 상세 요청: id={}", id);
        ProblemResponse problem = problemService.findById(id);
        model.addAttribute("problem", problem);

        Object principal = session.getAttribute("loginUser");
        if (principal instanceof UserResponse user) {
            model.addAttribute("loginUser", user);
        }
        return "problems/detail";
    }

    /** ✅ 문제 삭제 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        log.info("[POST] 문제 삭제 요청: id={}", id);
        problemService.delete(id);
        return "redirect:/problems";
    }

    /** ✅ 문제 풀이 페이지 */
    @GetMapping("/solve")
    public String solvePage(HttpSession session, Model model) {
        log.info("[GET] 문제 풀이 페이지 요청");
        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");
        Long userId = (loginUser != null ? loginUser.id() : 0L);
        model.addAttribute("userId", userId);
        model.addAttribute("loginUser", loginUser);
        return "problems/solve";
    }

    // ---------- APIs ----------

    /** ✅ 과목 리스트 API */
    @ResponseBody
    @GetMapping("/api/subjects")
    public List<Subject> getSubjects() {
        log.info("[API] 과목 리스트 요청");
        return subjectRepository.findAll();
    }

    /** ✅ 회차 리스트 API */
    @ResponseBody
    @GetMapping("/api/rounds")
    public List<RoundDto> getRounds(@RequestParam("subjectId") Long subjectId) {
        log.info("[API] 회차 리스트 요청: subjectId={}", subjectId);
        return roundRepository.findBySubject_Id(subjectId)
                .stream()
                .map(r -> new RoundDto(r.getId(), r.getRoundNumber(), r.getName()))
                .toList();
    }

    /** ✅ 목차 리스트 API */
    @ResponseBody
    @GetMapping("/api/units")
    public List<UnitDto> getUnits(@RequestParam("subjectId") Long subjectId) {
        log.info("[API] 목차 리스트 요청: subjectId={}", subjectId);
        return unitRepository.findBySubject_Id(subjectId)
                .stream()
                .map(u -> new UnitDto(u.getId(), u.getName()))
                .toList();
    }

    /** ✅ 문제 리스트 API (이미지 경로 보정) */
    @ResponseBody
    @GetMapping("/api/problems")
    public List<ProblemResponseForSolve> getProblems(
            @RequestParam Long subjectId,
            @RequestParam(required = false) List<Long> roundIds,
            @RequestParam(required = false) List<Long> unitIds
    ) {
        log.info("[API] 문제 리스트 요청: subjectId={}, roundIds={}, unitIds={}", subjectId, roundIds, unitIds);

        return problemRepository.findByFilters(subjectId, roundIds, unitIds)
                .stream()
                .map(problem -> new ProblemResponseForSolve(
                        problem.getId(),
                        problem.getTitle(),
                        problem.getViewContent(),
                        // ✅ 프론트가 그대로 src에 꽂을 수 있도록 절대경로 형태로 보정
                        problem.getImageUrl() != null ? ("/uploads/" + problem.getImageUrl()) : null,
                        List.of(
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice1()),
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice2()),
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice3()),
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice4()),
                                new ProblemResponseForSolve.ChoiceDto(problem.getChoice5())
                        ),
                        problem.getAnswer()
                ))
                .toList();
    }

    /** ✅ 즉시 채점 API: 선택 즉시 채점/기록 */
    @ResponseBody
    @PostMapping(value = "/answer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AnswerResponse submitAnswer(@RequestBody AnswerRequest request, HttpSession session) {
        log.info("[API] 채점 요청: problemId={}, selected={}", request.problemId(), request.selected());

        // 1) 문제 로드
        var problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("문제가 존재하지 않습니다."));

        // 2) 채점
        boolean correct = (problem.getAnswer() != null && problem.getAnswer().equals(request.selected()));

        // 3) (선택) 기록/오답노트 저장 포인트
        // TODO: loginUser가 있을 때 풀이기록/오답노트 저장 (서비스에 연결)
        // Object principal = session.getAttribute("loginUser");
        // if (principal instanceof UserResponse user) { problemService.recordAnswer(user.id(), problem.getId(), request.selected(), correct); }

        // 4) 해설(있으면)
        String explanation = null;
        // 만약 엔티티에 getExplanation()/getSolution() 등이 있다면 아래처럼 채우세요.
        // explanation = problem.getExplanation();

        return new AnswerResponse(correct, problem.getAnswer(), explanation);
    }

    // ====== 내부 DTO (컨트롤러 전용, 간단하게 둠) ======
    public record AnswerRequest(Long problemId, Integer selected) {}
    public record AnswerResponse(boolean correct, Integer answer, String explanation) {}
}
