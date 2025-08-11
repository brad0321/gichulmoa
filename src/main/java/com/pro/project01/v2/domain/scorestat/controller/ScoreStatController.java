package com.pro.project01.v2.domain.scorestat.controller;

import com.pro.project01.v2.domain.scorestat.dto.ScoreStatCreateRequest;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatResponse;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatUpdateRequest;
import com.pro.project01.v2.domain.scorestat.service.ScoreStatService;
import com.pro.project01.v2.domain.user.dto.UserResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/score-stats")
public class ScoreStatController {

    private final ScoreStatService scoreStatService;

    /* =======================
       View (Thymeleaf)
       ======================= */

    /** ✅ 점수 통계 전체 목록(관리/디버그용) */
    @GetMapping
    public String list(Model model) {
        List<ScoreStatResponse> stats = scoreStatService.findAll();
        model.addAttribute("stats", stats);
        return "scorestats/list"; // 뷰 템플릿 필요
    }

    /**
     * ✅ 특정 사용자 점수 통계 (세션 or 파라미터)
     * - userId 미전달 시 세션의 loginUser 사용
     * - 게스트면 로그인 페이지로 유도
     */
    @GetMapping("/user")
    public String userStats(@RequestParam(required = false) Long userId,
                            @SessionAttribute(value = "loginUser", required = false) UserResponse loginUser,
                            Model model) {
        Long targetUserId = (userId != null) ? userId : (loginUser != null ? loginUser.id() : null);
        if (targetUserId == null) {
            return "redirect:/login";
        }
        List<ScoreStatResponse> stats = scoreStatService.findByUserId(targetUserId);
        model.addAttribute("stats", stats);
        model.addAttribute("targetUserId", targetUserId);
        return "scorestats/user-list"; // 뷰 템플릿 필요
    }

    /** ✅ 점수 통계 상세 보기 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ScoreStatResponse stat = scoreStatService.findById(id);
        model.addAttribute("stat", stat);
        return "scorestats/detail"; // 뷰 템플릿 필요
    }

    /** ✅ 점수 통계 등록 */
    @PostMapping("/new")
    public String create(@ModelAttribute ScoreStatCreateRequest request) {
        scoreStatService.create(request);
        return "redirect:/score-stats/user?userId=" + request.userId();
    }

    /** ✅ 점수 통계 수정 */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute ScoreStatUpdateRequest request) {
        scoreStatService.update(id, request);
        return "redirect:/score-stats/" + id;
    }

    /** ✅ 점수 통계 삭제 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        scoreStatService.delete(id);
        return "redirect:/score-stats";
    }

    /* =======================
       JSON API (대시보드/차트용)
       ======================= */

    /** ✅ [API] 특정 사용자 통계 목록(JSON) */
    @GetMapping("/api/user")
    @ResponseBody
    public ResponseEntity<List<ScoreStatResponse>> apiUserStats(
            @RequestParam(required = false) Long userId,
            @SessionAttribute(value = "loginUser", required = false) UserResponse loginUser) {
        Long targetUserId = (userId != null) ? userId : (loginUser != null ? loginUser.id() : null);
        if (targetUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(scoreStatService.findByUserId(targetUserId));
    }

    /** ✅ [API] 전체 통계 목록(JSON) — 관리자/진단용 */
    @GetMapping("/api")
    @ResponseBody
    public List<ScoreStatResponse> apiAll() {
        return scoreStatService.findAll();
    }

    /** ✅ [API] 단건 조회(JSON) */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ScoreStatResponse apiDetail(@PathVariable Long id) {
        return scoreStatService.findById(id);
    }

    /** ✅ [API] 생성(JSON) */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Void> apiCreate(@RequestBody ScoreStatCreateRequest request) {
        scoreStatService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** ✅ [API] 수정(JSON) */
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> apiUpdate(@PathVariable Long id,
                                          @RequestBody ScoreStatUpdateRequest request) {
        scoreStatService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    /** ✅ [API] 삭제(JSON) */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> apiDelete(@PathVariable Long id) {
        scoreStatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
