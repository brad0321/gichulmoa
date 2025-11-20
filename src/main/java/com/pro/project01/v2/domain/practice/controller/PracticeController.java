// src/main/java/com/pro/project01/v2/domain/practice/controller/PracticeController.java
package com.pro.project01.v2.domain.practice.controller;

import com.pro.project01.v2.domain.practice.service.PracticeService;
import com.pro.project01.v2.domain.practice.service.PracticeService.StartRes;
import com.pro.project01.v2.domain.practice.service.PracticeService.AnswerRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/problems/practice")
public class PracticeController {

    private final PracticeService practiceService;

    /** ✅ 세션 시작 */
    @PostMapping("/start")
    public StartRes start(@RequestBody StartReq req,
                          @SessionAttribute(name="loginUser", required=false) Object loginUser) {

        Long userId = null;
        if (loginUser instanceof com.pro.project01.v2.domain.user.dto.UserResponse u) {
            userId = u.id();
        }

        // ✅ 빈 리스트([])를 null로 변환 → 전체 선택 처리
        List<Long> safeRounds = (req.roundIds == null || req.roundIds.isEmpty()) ? null : req.roundIds;
        List<Long> safeUnits  = (req.unitIds  == null || req.unitIds.isEmpty())  ? null : req.unitIds;

        return practiceService.start(userId, req.subjectId, safeRounds, safeUnits);
    }

    /** ✅ 즉시 답안 제출 */
    @PostMapping("/answer")
    public AnswerRes answer(@RequestBody AnswerReq req,
                            @SessionAttribute(name="loginUser", required=false) Object loginUser) {
        Long userId = null;
        if (loginUser instanceof com.pro.project01.v2.domain.user.dto.UserResponse u) {
            userId = u.id();
        }
        return practiceService.answer(req.sessionId, req.itemId, req.selected, userId);
    }

    /** ✅ 보기 소거 토글 */
    @PatchMapping("/eliminate")
    public void eliminate(@RequestParam Long itemId, @RequestParam int choiceNo) {
        practiceService.toggleEliminate(itemId, choiceNo);
    }

    // ---- 내부 record DTO ----
    public record StartReq(Long subjectId, List<Long> roundIds, List<Long> unitIds) {}
    public record AnswerReq(Long sessionId, Long itemId, int selected) {}
}
