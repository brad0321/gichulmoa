package com.pro.project01.v2.domain.problem.controller;

import com.pro.project01.v2.domain.practice.service.PracticeService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final PracticeService practiceService;
    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    private static final Path UPLOAD_DIR = Paths.get("src/main/resources/static/uploads");
    private static final Set<String> ALLOWED_IMG_EXT = Set.of("png","jpg","jpeg","gif","webp");
    private static final Set<String> ALLOWED_IMG_MIME = Set.of("image/png","image/jpeg","image/gif","image/webp");

    /** ✅ 문제 목록 */
    @GetMapping
    public String list(Model model,
                       @SessionAttribute(value = "loginUser", required = false) UserResponse loginUser) {
        List<ProblemResponse> problems = problemService.findAll();
        log.info("[GET] 문제 목록 요청, size={}", problems.size());
        model.addAttribute("problems", problems);
        // 로그인 여부와 관계없이 모델에 loginUser를 추가하여 템플릿에서 안전하게 참조할 수 있도록 한다.
        model.addAttribute("loginUser", loginUser);
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
        return "problems/new"; // 경로 수정
    }

    /** ✅ 문제 등록 처리 (+ 보기별 해설 exp1~5 수신) */
    @PostMapping("/new")
    public String create(@ModelAttribute ProblemRequest request,
                         @RequestParam(value="imageFile", required=false) MultipartFile imageFile,
                         @RequestParam(value="exp1", required=false) String exp1,
                         @RequestParam(value="exp2", required=false) String exp2,
                         @RequestParam(value="exp3", required=false) String exp3,
                         @RequestParam(value="exp4", required=false) String exp4,
                         @RequestParam(value="exp5", required=false) String exp5,
                         RedirectAttributes redirectAttributes) throws IOException {
        log.info("[POST] 문제 등록 요청: {}", safeLog(request));

        // 필수 입력값 누락 시 다시 폼으로 이동
        if (request.title() == null || request.answer() == null ||
                request.subjectId() == null || request.roundId() == null || request.unitId() == null) {
            redirectAttributes.addFlashAttribute("error", "필수 항목을 모두 입력/선택해주세요.");
            return "redirect:/problems/new";
        }

        String imagePath = storeImageIfPresent(imageFile);
        // List.of(...) 는 null 요소를 허용하지 않아 expX 가 비어있을 때 NPE가 발생한다.
        // Arrays.asList(...) 를 사용하여 null을 포함한 목록을 전달한다.
        problemService.create(request, imagePath, Arrays.asList(exp1,exp2,exp3,exp4,exp5));
        log.info("문제 등록 완료");
        redirectAttributes.addFlashAttribute("msg", "문제가 등록되었습니다.");
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

    /**
     * ✅ 문제 수정 처리
     * - removeImage=true면 업로드 무시하고 이미지 제거 시그널 전달(여기선 ""로 약속)
     * - 새 파일이 있으면 저장 후 경로 전달
     * - 아무것도 없으면 imagePath=null 전달(이미지 변경 없음)
     *
     * ProblemService.update 시그니처 예시:
     *   update(Long id, ProblemRequest req, String imagePath, List<String> expList)
     *   - imagePath == null   : 이미지 변경 없음
     *   - imagePath == ""     : 이미지 제거
     *   - imagePath == "file" : 해당 파일명으로 교체
     */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute ProblemRequest request,
                         @RequestParam(value="imageFile", required=false) MultipartFile imageFile,
                         @RequestParam(value="removeImage", required=false) Boolean removeImage,
                         @RequestParam(value="exp1", required=false) String exp1,
                         @RequestParam(value="exp2", required=false) String exp2,
                         @RequestParam(value="exp3", required=false) String exp3,
                         @RequestParam(value="exp4", required=false) String exp4,
                         @RequestParam(value="exp5", required=false) String exp5) throws IOException {
        log.info("[POST] 문제 수정 요청: id={}, data={}", id, safeLog(request));

        String imagePath;
        if (Boolean.TRUE.equals(removeImage)) {
            imagePath = ""; // 제거 시그널
        } else {
            imagePath = storeImageIfPresent(imageFile); // 없으면 null
        }

        // 보기 해설 중 일부가 비어있을 경우 List.of(...) 가 NPE를 유발하므로 Arrays.asList 사용
        problemService.update(id, request, imagePath, Arrays.asList(exp1,exp2,exp3,exp4,exp5));
        log.info("문제 수정 완료: id={}", id);
        return "redirect:/problems/" + id;
    }

    /** ✅ 문제 상세 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @SessionAttribute(value = "loginUser", required = false) UserResponse loginUser) {
        log.info("[GET] 문제 상세 요청: id={}", id);
        ProblemResponse problem = problemService.findById(id);
        model.addAttribute("problem", problem);
        if (loginUser != null) model.addAttribute("loginUser", loginUser);
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
    public String solvePage(@SessionAttribute(value="loginUser", required=false) UserResponse loginUser,
                            Model model) {
        log.info("[GET] 문제 풀이 페이지 요청");
        Long userId = (loginUser != null ? loginUser.id() : 0L);
        model.addAttribute("userId", userId);
        model.addAttribute("loginUser", loginUser);
        return "problems/solve";
    }

    // ---------- APIs (목록/필터) ----------

    /** ✅ 과목 리스트 API */
    @ResponseBody
    @GetMapping("/api/subjects")
    public List<Subject> getSubjects() {
        log.info("[API] 과목 리스트 요청");
        return subjectRepository.findAll();
    }

    /** ✅ 회차 리스트 API (정렬 ASC, 널가드) */
    @ResponseBody
    @GetMapping("/api/rounds")
    public List<RoundDto> getRounds(@RequestParam("subjectId") Long subjectId) {
        log.info("[API] 회차 리스트 요청: subjectId={}", subjectId);
        return roundRepository.findBySubject_Id(subjectId).stream()
                .sorted(Comparator.comparingInt(r -> Optional.ofNullable(r.getRoundNumber()).orElse(0)))
                .map(r -> new RoundDto(r.getId(), r.getRoundNumber(), r.getName()))
                .collect(Collectors.toList());
    }

    /** ✅ 단원 리스트 API */
    @ResponseBody
    @GetMapping("/api/units")
    public List<UnitDto> getUnits(@RequestParam("subjectId") Long subjectId) {
        log.info("[API] 단원 리스트 요청: subjectId={}", subjectId);
        return unitRepository.findBySubject_Id(subjectId)
                .stream()
                .map(u -> new UnitDto(u.getId(), u.getName()))
                .toList();
    }

    /**
     * ✅ 문제 리스트 API (카운트/미리보기용)
     * - 프론트에서 개수 집계 및 일부 미리보기(지문/보기/이미지 절대경로) 용도
     */
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

    // ---------- 연습(기출 조합) 모드 API ----------

    // 연습 세션 시작
    @PostMapping(value="/practice/start", consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PracticeStartResponse startPractice(@RequestBody StartReq req,
                                               @SessionAttribute(value="loginUser", required=false) UserResponse user){
        if (user == null) throw new IllegalStateException("로그인 후 이용해주세요.");
        var r = practiceService.start(user.id(), req.subjectId(), nullIfEmpty(req.roundIds()), nullIfEmpty(req.unitIds()));

        // ★ 여기서 서비스 DTO → 컨트롤러 DTO로 매핑
        var mapped = r.firstPage().stream()
                .map(q -> new QuestionDto(
                        q.itemId(),
                        q.problemId(),
                        q.title(),
                        q.viewContent(),
                        q.imageUrl(),
                        q.choices(),
                        q.answer()
                ))
                .toList();

        return new PracticeStartResponse(r.sessionId(), mapped);
    }

    @GetMapping("/play")
    public String playPage(@SessionAttribute(value = "loginUser", required = false) UserResponse loginUser,
                           Model model) {
        Long userId = (loginUser != null) ? loginUser.id() : 0L;
        model.addAttribute("userId", userId);
        model.addAttribute("loginUser", loginUser);
        return "problems/play"; // -> templates/problems/play.html
    }

    // (선택) 과거 링크 호환: /exams 로 들어오면 /problems/play 로 보냄
    @GetMapping("/exams")
    public String examsRedirect() {
        return "redirect:/problems/play";
    }

    /** ✅ 연습 즉시 채점 */
    @PostMapping(value="/practice/answer", consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AnswerResponse practiceAnswer(@RequestBody PracticeAnswerReq req,
                                         @SessionAttribute(value="loginUser", required=false) UserResponse user){
        if (user == null) throw new IllegalStateException("로그인 후 이용해주세요.");
        var r = practiceService.answer(req.sessionId(), req.itemId(), req.selected(), user.id());
        return new AnswerResponse(r.correct(), r.answer(), r.explanation());
    }

    /** ✅ 소거법 토글 (세션 상태에 저장) */
    @PatchMapping("/practice/eliminate")
    @ResponseBody
    public void toggleEliminate(@RequestParam Long itemId, @RequestParam Integer choiceNo){
        practiceService.toggleEliminate(itemId, choiceNo);
    }

    // ---------- 내부 DTO (컨트롤러 전용) ----------
    public record StartReq(Long subjectId, List<Long> roundIds, List<Long> unitIds) {}
    public record PracticeAnswerReq(Long sessionId, Long itemId, Integer selected) {}

    /** 프론트에서 바로 쓰기 좋게 납작한 질문 DTO */
    public record PracticeStartResponse(Long sessionId, List<QuestionDto> firstPage) {}
    public record QuestionDto(Long itemId, Long problemId, String title, String viewContent,
                              String imageUrl, List<String> choices, Integer answer) {}
    public record AnswerResponse(boolean correct, Integer answer, String explanation) {}

    // ---------- 유틸 ----------
    private static List<Long> nullIfEmpty(List<Long> list){
        return (list == null || list.isEmpty()) ? null : list;
    }

    private String storeImageIfPresent(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String ext = extOf(original);
        String contentType = Optional.ofNullable(file.getContentType()).orElse("");

        if (!ALLOWED_IMG_EXT.contains(ext) || !ALLOWED_IMG_MIME.contains(contentType)) {
            throw new IOException("허용되지 않는 이미지 형식입니다.");
        }

        if (!Files.exists(UPLOAD_DIR)) Files.createDirectories(UPLOAD_DIR);

        String stored = UUID.randomUUID() + "." + ext;
        Path target = UPLOAD_DIR.resolve(stored);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        log.info("이미지 업로드 완료: {} -> {}", original, stored);
        return stored;
    }

    private static String extOf(String filename){
        String name = Path.of(filename).getFileName().toString();
        int dot = name.lastIndexOf('.');
        return (dot >= 0 && dot < name.length()-1) ? name.substring(dot+1).toLowerCase(Locale.ROOT) : "";
    }

    private static String safeLog(ProblemRequest r){
        if (r == null) return "null";
        return "ProblemRequest(title=%s, answer=%s, subjectId=%s, roundId=%s, unitId=%s)".formatted(
                r.title(), r.answer(), r.subjectId(), r.roundId(), r.unitId()
        );
    }
}
