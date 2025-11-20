// src/main/java/com/pro/project01/v2/domain/problem/controller/ProblemController.java
package com.pro.project01.v2.domain.problem.controller;

import com.pro.project01.v2.domain.practice.service.PracticeService;
import com.pro.project01.v2.domain.problem.dto.*;
import com.pro.project01.v2.domain.problem.dto.ProblemCodeDtos.*;
import com.pro.project01.v2.domain.problem.service.ProblemService;
import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.unit.entity.Unit;
import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final PracticeService practiceService;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;

    private static final Path UPLOAD_DIR = Paths.get("src/main/resources/static/uploads");
    private static final Set<String> ALLOWED_IMG_EXT  = Set.of("png","jpg","jpeg","gif","webp");
    private static final Set<String> ALLOWED_IMG_MIME = Set.of("image/png","image/jpeg","image/gif","image/webp");

    /* =========================================================================
       목록(무한스크롤) - 템플릿만 제공, 데이터는 /list/api 로드
       ========================================================================= */
    @GetMapping
    public String listPage(@RequestParam(required = false) Long subjectId,
                           @RequestParam(required = false, name = "roundIds") List<Long> roundIds,
                           @RequestParam(required = false, name = "unitIds") List<Long> unitIds,
                           @RequestParam(required = false) String q,
                           @RequestParam(defaultValue = "20") int size,
                           HttpSession session,
                           Model model) {

        // 🔹 회차/단원: 0, null, 음수 제거 → 양수 ID만 남김
        if (roundIds == null) {
            roundIds = Collections.emptyList();
        } else {
            roundIds = roundIds.stream()
                    .filter(id -> id != null && id > 0)
                    .toList();
        }

        if (unitIds == null) {
            unitIds = Collections.emptyList();
        } else {
            unitIds = unitIds.stream()
                    .filter(id -> id != null && id > 0)
                    .toList();
        }

        log.info("[GET] 문제 목록 템플릿 subjectId={}, roundIds={}, unitIds={}, q={}",
                subjectId, roundIds, unitIds, q);

        // 로그인 사용자
        Object principal = session.getAttribute("loginUser");
        if (principal instanceof UserResponse user) {
            model.addAttribute("loginUser", user);
        }

        // 🔹 필터 옵션(셀렉트박스에 뿌릴 목록)
        model.addAttribute("subjects", subjectRepository.findAll());
        if (subjectId != null && subjectId > 0) {
            // 과목 선택 시: 해당 과목의 회차/단원만
            model.addAttribute("rounds", roundRepository.findBySubject_Id(subjectId));
            model.addAttribute("units", unitRepository.findBySubject_Id(subjectId));
        } else {
            // 과목 전체: 전체 회차/단원
            model.addAttribute("rounds", roundRepository.findAll());
            model.addAttribute("units", unitRepository.findAll());
        }

        // 🔹 템플릿/JS에서 쓸 초기 파라미터 전달
        model.addAttribute("subjectId", subjectId);
        model.addAttribute("selectedRoundIds", roundIds); // ✅ 다중 선택 유지용
        model.addAttribute("selectedUnitIds", unitIds);   // ✅ 다중 선택 유지용
        model.addAttribute("q", q);
        model.addAttribute("size", size);

        return "problems/list";
    }

    /** ✅ 무한스크롤 JSON API (단일 커서 cursorId 기반, 필터는 복수 회차/단원) */
    @GetMapping(value="/list/api", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ProblemListSliceResponse3Cursor> listApi(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false, name = "roundIds") List<Long> roundIds,
            @RequestParam(required = false, name = "unitIds") List<Long> unitIds,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        // 🔹 회차/단원 필터 정리
        if (roundIds == null) {
            roundIds = Collections.emptyList();
        } else {
            roundIds = roundIds.stream()
                    .filter(id -> id != null && id > 0)
                    .toList();
        }

        if (unitIds == null) {
            unitIds = Collections.emptyList();
        } else {
            unitIds = unitIds.stream()
                    .filter(id -> id != null && id > 0)
                    .toList();
        }

        // ⚠️ ProblemService.getProblemList 시그니처를 List 기반으로 수정해야 함
        Slice<ProblemListItemView> slice = problemService.getProblemList(
                subjectId, roundIds, unitIds, q, cursorId, size
        );

        var items = slice.getContent().stream()
                .map(ProblemListItemDto::from)
                .collect(Collectors.toList());

        Long nextCursorId = null;

        List<ProblemListItemView> all = slice.getContent();
        if (!all.isEmpty()) {
            var last = all.get(all.size() - 1);
            Integer spn = last.getSubjectProblemNo();
            if (spn != null) {
                nextCursorId = spn.longValue();
            }
        }
        return ResponseEntity.ok(
                new ProblemListSliceResponse3Cursor(
                        items,
                        slice.hasNext(),
                        null,   // nextRoundNumber (안 쓰므로 그대로 null)
                        null,   // nextRoundProblemNo (안 쓰므로 그대로 null)
                        nextCursorId  // ✅ 이제 subject_problem_no 기반 커서
                )
        );
    }

    /* =========================================================================
       등록/수정/상세/삭제
       ========================================================================= */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("problem", new ProblemRequest(
                null, null, null, null, null, null, null,
                null,
                null, null, null,
                null,
                null,
                null,
                null
        ));
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", Collections.emptyList());
        model.addAttribute("units", Collections.emptyList());
        return "problems/new";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute ProblemRequest request,
                         @RequestParam(value="imageFile", required=false) MultipartFile imageFile,
                         RedirectAttributes redirectAttributes) throws IOException {

        if (request.title() == null || request.answer() == null ||
                request.subjectId() == null || request.roundId() == null || request.unitId() == null) {
            redirectAttributes.addFlashAttribute("error", "필수 항목을 모두 입력/선택해주세요.");
            return "redirect:/problems/new";
        }

        String imagePath = storeImageIfPresent(imageFile);

        problemService.create(request, imagePath, List.of(
                request.choice1(),
                request.choice2(),
                request.choice3(),
                request.choice4(),
                request.choice5()
        ));

        redirectAttributes.addFlashAttribute("msg", "문제가 등록되었습니다.");
        return "redirect:/problems";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ProblemResponse problem = problemService.findById(id);
        model.addAttribute("problem", problem);
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("rounds", Collections.emptyList());
        model.addAttribute("units", Collections.emptyList());
        return "problems/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute ProblemRequest request,
                         @RequestParam(value="imageFile", required=false) MultipartFile imageFile,
                         @RequestParam(value="removeImage", required=false) Boolean removeImage) throws IOException {

        String imagePath = Boolean.TRUE.equals(removeImage) ? null : storeImageIfPresent(imageFile);

        problemService.update(id, request, imagePath, List.of(
                request.choice1(),
                request.choice2(),
                request.choice3(),
                request.choice4(),
                request.choice5()
        ));

        return "redirect:/problems/" + id;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        ProblemResponse problem = problemService.findById(id);
        model.addAttribute("problem", problem);
        Object principal = session.getAttribute("loginUser");
        if (principal instanceof UserResponse user) {
            model.addAttribute("loginUser", user);
        }
        return "problems/detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes) {
        try {
            problemService.delete(id);
            redirectAttributes.addFlashAttribute("msg", "문제가 삭제되었습니다.");
        } catch (IllegalStateException e) {
            // 위 delete() 에서 wrap 한 예외
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/problems");
    }


    /* =========================================================================
       풀이/정렬 이동
       ========================================================================= */
    @GetMapping("/solve")
    public String solvePage(HttpSession session, Model model) {
        Object principal = session.getAttribute("loginUser");
        if (principal instanceof UserResponse user) {
            model.addAttribute("loginUser", user);
            model.addAttribute("userId", user.id());
        } else {
            model.addAttribute("userId", 0L);
        }
        return "problems/solve";
    }

    @PostMapping("/{id}/moveUp")
    public String moveUp(@PathVariable Long id, HttpServletRequest request) {
        problemService.moveUp(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/problems");
    }

    @PostMapping("/{id}/moveDown")
    public String moveDown(@PathVariable Long id, HttpServletRequest request) {
        problemService.moveDown(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/problems");
    }

    /* =========================================================================
       Ajax API - 과목/회차/단원/개수
       ========================================================================= */
    @GetMapping(value="/api/rounds", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> getRounds(@RequestParam Long subjectId) {
        List<Round> rounds = roundRepository.findBySubject_Id(subjectId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Round r : rounds) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("name", r.getName());
            Integer roundNumber = null;
            if (r.getRoundNumber() != null) {
                roundNumber = r.getRoundNumber().intValue();
            } else {
                String text = Optional.ofNullable(r.getName()).orElse("");
                Matcher m = Pattern.compile("(\\d{1,3})").matcher(text);
                if (m.find()) roundNumber = Integer.parseInt(m.group(1));
            }
            map.put("roundNumber", roundNumber);
            result.add(map);
        }
        return result;
    }

    @GetMapping(value="/api/units", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> getUnits(@RequestParam Long subjectId) {
        List<Unit> units = unitRepository.findBySubject_Id(subjectId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Unit u : units) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            result.add(map);
        }
        return result;
    }

    @GetMapping(value="/api/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> getSubjects() {
        List<Map<String, Object>> result = new ArrayList<>();
        subjectRepository.findAll().forEach(subject -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", subject.getId());
            map.put("name", subject.getName());
            result.add(map);
        });
        return result;
    }

    @GetMapping(value="/api/problems/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getProblemCount(@RequestParam(required = false) Long subjectId,
                                               @RequestParam(required = false, name = "roundIds") List<Long> roundIds,
                                               @RequestParam(required = false, name = "unitIds") List<Long> unitIds) {

        // 🔹 과목: 0, null → 전체
        Long safeSubjectId = (subjectId != null && subjectId > 0) ? subjectId : null;

        // 🔹 회차/단원: 0, null, 음수 제거 → 비어 있으면 전체 취급(null)
        List<Long> safeRounds = null;
        if (roundIds != null) {
            safeRounds = roundIds.stream()
                    .filter(id -> id != null && id > 0)
                    .toList();
            if (safeRounds.isEmpty()) safeRounds = null;
        }

        List<Long> safeUnits = null;
        if (unitIds != null) {
            safeUnits = unitIds.stream()
                    .filter(id -> id != null && id > 0)
                    .toList();
            if (safeUnits.isEmpty()) safeUnits = null;
        }

        long count = problemService.countByFilters(safeSubjectId, safeRounds, safeUnits);
        return Map.of("count", count);
    }

    /* =========================================================================
       코드 API
       ========================================================================= */
    @GetMapping(value="/{id}/api", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CodesResponse> getCodes(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getCodes(id));
    }

    @PostMapping(value="/{id}/codes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCodes(@PathVariable Long id,
                                         @Valid @RequestBody UpdateCodesRequest req) {
        problemService.updateCodes(id, req);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /* =========================================================================
       공통 예외 처리
       ========================================================================= */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "message", e.getMessage()));
    }

    /* =========================================================================
       파일 저장 유틸
       ========================================================================= */
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
        return stored;
    }

    private static String extOf(String filename) {
        String name = Path.of(filename).getFileName().toString();
        int dot = name.lastIndexOf('.');
        return (dot >= 0 && dot < name.length() - 1)
                ? name.substring(dot + 1).toLowerCase(Locale.ROOT)
                : "";
    }
}
