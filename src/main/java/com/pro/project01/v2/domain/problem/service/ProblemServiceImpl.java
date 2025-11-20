package com.pro.project01.v2.domain.problem.service;

import com.pro.project01.v2.domain.calculation.repository.CalculationProblemRepository;
import com.pro.project01.v2.domain.explanation.repository.ExplanationRepository;
import com.pro.project01.v2.domain.practice.repository.PracticeSessionItemRepository;
import com.pro.project01.v2.domain.problem.dto.ProblemCodeDtos.CodesResponse;
import com.pro.project01.v2.domain.problem.dto.ProblemCodeDtos.UpdateCodesRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemListItemView;
import com.pro.project01.v2.domain.problem.dto.ProblemRequest;
import com.pro.project01.v2.domain.problem.dto.ProblemResponse;
import com.pro.project01.v2.domain.problem.entity.Problem;
import com.pro.project01.v2.domain.problem.repository.ProblemRepository;
import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.unit.entity.Unit;
import com.pro.project01.v2.domain.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;
    private final UnitRepository unitRepository;
    private final CalculationProblemRepository calculationProblemRepository;
    private final ExplanationRepository explanationRepository;
    private final PracticeSessionItemRepository practiceSessionItemRepository;

    /* ===========================
       🔹 기본 조회
       =========================== */
    @Override
    @Transactional(readOnly = true)
    public List<ProblemResponse> findAll() {
        return problemRepository.findAllByCustomOrder()
                .stream()
                .map(ProblemResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemResponse findById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + id));
        return ProblemResponse.fromEntity(problem);
    }

    /* ===========================
       🔹 생성
       =========================== */
    @Override
    public Long create(ProblemRequest request, String imagePath) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("과목이 없습니다. id=" + request.subjectId()));
        Round round = roundRepository.findById(request.roundId())
                .orElseThrow(() -> new IllegalArgumentException("회차가 없습니다. id=" + request.roundId()));
        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new IllegalArgumentException("단원이 없습니다. id=" + request.unitId()));

        Byte roundProblemNo = requireRange(toByte(request.roundProblemNo()), 1, 40, "회차 내 번호는 1~40 범위여야 합니다.");
        Byte unitProblemNo  = requireRange(toByte(request.unitProblemNo()), 1, 99, "단원 내 순번은 1~99 범위여야 합니다.");

        Problem problem = Problem.builder()
                .title(request.title())
                .viewContent(request.viewContent())
                .imageUrl(imagePath)
                .choice1(request.choice1())
                .choice2(request.choice2())
                .choice3(request.choice3())
                .choice4(request.choice4())
                .choice5(request.choice5())
                .answer(request.answer())
                .subject(subject)
                .round(round)
                .unit(unit)
                .orderNo(nextOrderNo())
                .roundProblemNo(roundProblemNo)
                .unitProblemNo(unitProblemNo)
                .subjectCode(subject.getCode())
                .unitSeqCode(unit.getSeqCode())
                .roundNumber(round.getRoundNumber())
                .build();

        problemRepository.save(problem);
        return problem.getId();
    }

    @Override
    public Long create(ProblemRequest request, String imagePath, List<String> exps) {
        Long id = create(request, imagePath);
        upsertChoiceExplanations(id, exps);
        return id;
    }

    /* ===========================
       🔹 수정
       =========================== */
    @Override
    public void update(Long id, ProblemRequest request, String imagePath) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + id));

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("과목이 없습니다. id=" + request.subjectId()));
        Round round = roundRepository.findById(request.roundId())
                .orElseThrow(() -> new IllegalArgumentException("회차가 없습니다. id=" + request.roundId()));
        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new IllegalArgumentException("단원이 없습니다. id=" + request.unitId()));

        Byte roundProblemNo = requireRange(toByte(request.roundProblemNo()), 1, 40, "회차 내 번호는 1~40 범위여야 합니다.");
        Byte unitProblemNo  = requireRange(toByte(request.unitProblemNo()), 1, 99, "단원 내 순번은 1~99 범위여야 합니다.");

        problem.update(
                request.title(),
                request.viewContent(),
                (imagePath != null ? imagePath : problem.getImageUrl()),
                request.choice1(),
                request.choice2(),
                request.choice3(),
                request.choice4(),
                request.choice5(),
                request.answer(),
                subject,
                round,
                unit,
                roundProblemNo,
                subject.getCode(),
                unit.getSeqCode(),
                unitProblemNo,
                null
        );

        setRoundNumber(problem, round.getRoundNumber());
    }

    @Override
    public void update(Long id, ProblemRequest request, String imagePath, List<String> exps) {
        update(id, request, imagePath);
        upsertChoiceExplanations(id, exps);
    }

    /* ===========================
       🔹 삭제
       =========================== */
    @Override
    public void delete(Long id) {
        try {
            // 1️⃣ 연습 세션 아이템(풀이 세션에서 사용된 문제 기록) 먼저 삭제
            practiceSessionItemRepository.deleteByProblem_Id(id);

            // 2️⃣ 계산형 문제 매핑 삭제 (이미 있던 코드)
            calculationProblemRepository.deleteByProblemId(id);

            // 3️⃣ 보기 해설 삭제 (이미 있던 코드)
            explanationRepository.deleteAll(
                    explanationRepository.findByProblem_IdOrderByChoiceNoAsc(id)
            );

            // 4️⃣ 마지막으로 문제 삭제
            problemRepository.deleteById(id);

        } catch (Exception e) {
            log.error("문제 삭제 중 오류 발생. id={}", id, e);
            // 필요하면 메시지를 좀 더 친절하게 감싸서 던져도 됨
            throw new IllegalStateException("문제를 삭제하는 중 오류가 발생했습니다.", e);
        }
    }

    /* ===========================
       🔹 검색/필터링
       =========================== */
    @Override
    @Transactional(readOnly = true)
    public List<ProblemResponse> findByFilters(Long subjectId, List<Long> roundIds, List<Long> unitIds) {
        if (roundIds != null && roundIds.isEmpty()) return List.of();
        if (unitIds != null && unitIds.isEmpty()) return List.of();

        List<Long> safeRounds = (roundIds == null || roundIds.isEmpty()) ? null : roundIds;
        List<Long> safeUnits  = (unitIds == null || unitIds.isEmpty()) ? null : unitIds;

        return problemRepository.findByFilters(subjectId, safeRounds, safeUnits)
                .stream()
                .map(ProblemResponse::fromEntity)
                .toList();
    }

    /* ===========================
       🔹 순서 이동
       =========================== */
    @Override
    public void moveUp(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + id));

        List<Problem> prevList = problemRepository.findPrevByOrderNo(problem.getOrderNo(), PageRequest.of(0, 1));
        if (prevList.isEmpty()) return;

        Problem prev = prevList.get(0);
        int temp = problem.getOrderNo();
        problem.setOrderNo(prev.getOrderNo());
        prev.setOrderNo(temp);

        problemRepository.save(problem);
        problemRepository.save(prev);
    }

    @Override
    public void moveDown(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + id));

        List<Problem> nextList = problemRepository.findNextByOrderNo(problem.getOrderNo(), PageRequest.of(0, 1));
        if (nextList.isEmpty()) return;

        Problem next = nextList.get(0);
        int temp = problem.getOrderNo();
        problem.setOrderNo(next.getOrderNo());
        next.setOrderNo(temp);

        problemRepository.save(problem);
        problemRepository.save(next);
    }

    /* ===========================
       🔹 카운트
       =========================== */
    @Override
    @Transactional(readOnly = true)
    public long countByFilters(Long subjectId, List<Long> roundIds, List<Long> unitIds) {
        if (roundIds != null && roundIds.isEmpty()) return 0L;
        if (unitIds != null && unitIds.isEmpty()) return 0L;

        List<Long> safeRounds = (roundIds == null || roundIds.isEmpty()) ? null : roundIds;
        List<Long> safeUnits  = (unitIds == null || unitIds.isEmpty()) ? null : unitIds;

        return problemRepository.countByFilters(subjectId, safeRounds, safeUnits);
    }

    /* ======================================================
       ✅ 커서 기반 무한스크롤 목록 (Keyset Paging, 복수 회차/단원)
       ====================================================== */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProblemListItemView> getProblemList(
            Long subjectId,
            List<Long> roundIds,
            List<Long> unitIds,
            String q,
            Long cursorId,
            int size
    ) {
        final String keyword = (q == null || q.isBlank()) ? null : q;
        final int pageSize = Math.min(Math.max(size, 10), 50);

        // 🔹 과목: 0, null → 전체
        Long safeSubjectId = (subjectId != null && subjectId > 0) ? subjectId : null;

        // 🔹 회차/단원: 0, null, 음수 제거 → 비어 있으면 전체(null)
        List<Long> safeRoundIds = null;
        if (roundIds != null) {
            safeRoundIds = roundIds.stream()
                    .filter(id -> id != null && id > 0)
                    .toList();
            if (safeRoundIds.isEmpty()) safeRoundIds = null;
        }

        List<Long> safeUnitIds = null;
        if (unitIds != null) {
            safeUnitIds = unitIds.stream()
                    .filter(id -> id != null && id > 0)
                    .toList();
            if (safeUnitIds.isEmpty()) safeUnitIds = null;
        }

        Pageable pageablePlusOne = PageRequest.of(0, pageSize + 1);

        List<ProblemListItemView> rows = problemRepository.findNextPageForInfiniteScroll(
                safeSubjectId, safeRoundIds, safeUnitIds, keyword, cursorId, pageablePlusOne
        );

        boolean hasNext = rows.size() > pageSize;
        List<ProblemListItemView> content = hasNext
                ? new ArrayList<>(rows.subList(0, pageSize))
                : new ArrayList<>(rows);

        Long lastId = rows.isEmpty() ? null : rows.get(rows.size() - 1).getId();
        log.info("🧩 [getProblemList] subjectId={}, roundIds={}, unitIds={}, cursorId={}, lastId={}, total={}, hasNext={}",
                safeSubjectId, safeRoundIds, safeUnitIds, cursorId, lastId, rows.size(), hasNext);

        return new SliceImpl<>(content, PageRequest.of(0, pageSize), hasNext);
    }

    /* ===========================
       🔹 코드 관리
       =========================== */
    @Override
    @Transactional(readOnly = true)
    public CodesResponse getCodes(Long problemId) {
        Problem p = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제가 없습니다. id=" + problemId));

        return new CodesResponse(
                p.getId(),
                p.getRoundProblemNo() == null ? null : p.getRoundProblemNo().intValue(),
                p.getSubjectCode(),
                p.getUnitSeqCode(),
                p.getUnitProblemNo() == null ? null : p.getUnitProblemNo().intValue(),
                p.getRoundCode(),
                p.getUnitCode()
        );
    }

    @Override
    public void updateCodes(Long problemId, UpdateCodesRequest req) {
        Problem p = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제가 없습니다. id=" + problemId));

        int rpn = req.roundProblemNo();
        int upn = req.unitProblemNo();
        if (rpn < 1 || rpn > 40) throw new IllegalArgumentException("회차 내 번호는 1~40 범위여야 합니다.");
        if (upn < 1 || upn > 99) throw new IllegalArgumentException("단원 내 순번은 1~99 범위여야 합니다.");

        String sc  = req.subjectCode();
        String ucc = req.unitSeqCode();
        if (sc == null || sc.length() != 2)   throw new IllegalArgumentException("과목코드는 2자리 숫자여야 합니다.");
        if (ucc == null || ucc.length() != 3) throw new IllegalArgumentException("단원코드는 3자리 숫자여야 합니다.");

        p.updateCodes((byte) rpn, sc, ucc, (byte) upn);
    }

    /* ===========================
       🔹 내부 유틸
       =========================== */
    private static Byte toByte(Integer v) { return v == null ? null : v.byteValue(); }

    private static Byte requireRange(Byte v, int min, int max, String msg) {
        if (v == null || v < min || v > max) throw new IllegalArgumentException(msg);
        return v;
    }

    private int nextOrderNo() {
        Integer max = problemRepository.findMaxOrderNo();
        return (max == null ? 1 : max + 1);
    }

    private void upsertChoiceExplanations(Long problemId, List<String> exps) {
        if (exps == null) return;
        for (int i = 0; i < 5; i++) {
            String c = (exps.size() > i) ? exps.get(i) : null;
            explanationRepository.upsert(problemId, i + 1, c);
        }
    }

    private void setRoundNumber(Problem p, Short roundNumber) {
        try {
            Problem.class.getMethod("setRoundNumber", Short.class).invoke(p, roundNumber);
        } catch (NoSuchMethodException e) {
            try {
                Field f = Problem.class.getDeclaredField("roundNumber");
                f.setAccessible(true);
                f.set(p, roundNumber);
            } catch (Exception ignore) {}
        } catch (Exception ignored) {}
    }
}
