package com.pro.project01.v2.domain.practice.service;

import com.pro.project01.v2.domain.explanation.entity.Explanation;
import com.pro.project01.v2.domain.explanation.repository.ExplanationRepository;
import com.pro.project01.v2.domain.practice.entity.PracticeSession;
import com.pro.project01.v2.domain.practice.entity.PracticeSessionItem;
import com.pro.project01.v2.domain.practice.entity.SessionStatus;
import com.pro.project01.v2.domain.practice.repository.PracticeSessionItemRepository;
import com.pro.project01.v2.domain.practice.repository.PracticeSessionRepository;
import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.scorestat.service.ScoreStatService;
import com.pro.project01.v2.domain.solutionhistory.entity.SolutionHistory;
import com.pro.project01.v2.domain.solutionhistory.repository.SolutionHistoryRepository;
import com.pro.project01.v2.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PracticeService {

    private final PracticeSessionRepository sRepo;
    private final PracticeSessionItemRepository iRepo;
    private final ProblemRepository pRepo;
    private final ExplanationRepository expRepo;
    private final SolutionHistoryRepository hRepo;
    private final ScoreStatService scoreService;

    @Transactional
    public StartRes start(Long userId, Long subjectId, List<Long> roundIds, List<Long> unitIds){
        var problems = pRepo.findByFilters(subjectId, roundIds, unitIds);
        if (problems.isEmpty()) return new StartRes(null, List.of());

        var session = sRepo.save(PracticeSession.builder()
                .user(User.builder().id(userId).build())
                .status(SessionStatus.ACTIVE)
                .currentIndex(0)
                .subjectId(subjectId)
                .roundIds(roundIds == null ? new ArrayList<>() : new ArrayList<>(roundIds))
                .unitIds(unitIds == null ? new ArrayList<>() : new ArrayList<>(unitIds))
                .lastAccessedAt(LocalDateTime.now())
                .build());

        int idx = 0;
        for (Problem p: problems){
            iRepo.save(PracticeSessionItem.builder()
                    .session(session).problem(p)
                    .orderIndex(idx++)
                    .eliminatedMask(0)
                    .build());
        }
        return new StartRes(session.getId(), toDtos(session.getId(), problems));
    }

    @Transactional
    public AnswerRes answer(Long sessionId, Long itemId, int selected, Long userId){
        var item = iRepo.findById(itemId).orElseThrow();
        var p = item.getProblem();
        boolean correct = (p.getAnswer()!=null && Objects.equals(p.getAnswer(), selected));

        item.setSelectedChoiceNo(selected);
        item.setIsCorrect(correct);
        item.getSession().setLastAccessedAt(LocalDateTime.now());

        // 풀이 기록 (selectedChoiceNo 컬럼 존재해야 함)
        hRepo.save(SolutionHistory.builder()
                .user(User.builder().id(userId).build())
                .problem(p)
                .selectedChoiceNo(selected)
                .isCorrect(correct)
                .build());

        // 통계
        scoreService.record(userId, p.getSubject().getId(), p.getRound().getId(), correct);

        // 보기별 해설 (정답이면 정답 보기에 대한 해설, 오답이면 사용자가 고른 보기에 대한 해설)
        String exp = expRepo.findByProblem_IdAndChoiceNo(p.getId(), correct ? p.getAnswer() : selected)
                .map(Explanation::getContent).orElse(null);

        return new AnswerRes(correct, p.getAnswer(), exp);
    }

    @Transactional
    public void toggleEliminate(Long itemId, int choiceNo){
        var item = iRepo.findById(itemId).orElseThrow();
        int bit = 1 << (choiceNo - 1); // 1→1, 2→2, 3→4, 4→8, 5→16
        int mask = item.getEliminatedMask();
        item.setEliminatedMask((mask & bit) != 0 ? (mask ^ bit) : (mask | bit));
    }

    private List<QuestionDto> toDtos(Long sessionId, List<Problem> problems){
        List<QuestionDto> list = new ArrayList<>();
        var items = iRepo.findBySession_IdOrderByOrderIndexAsc(sessionId);

        for (int idx = 0; idx < problems.size(); idx++){
            Problem p = problems.get(idx);
            PracticeSessionItem item = items.get(idx);
            list.add(new QuestionDto(
                    item.getId(),
                    p.getId(),
                    p.getTitle(),
                    p.getViewContent(),
                    p.getImageUrl() == null ? null : "/uploads/" + p.getImageUrl(),
                    List.of(p.getChoice1(), p.getChoice2(), p.getChoice3(), p.getChoice4(), p.getChoice5()),
                    p.getAnswer()
            ));
        }
        return list;
    }

    // ====== records ======
    public record StartRes(Long sessionId, List<QuestionDto> firstPage) {}
    public record AnswerRes(boolean correct, Integer answer, String explanation) {}
    public record QuestionDto(Long itemId, Long problemId, String title, String viewContent,
                              String imageUrl, List<String> choices, Integer answer) {}
}
