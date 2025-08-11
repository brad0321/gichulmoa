package com.pro.project01.v2.domain.scorestat.service;

import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatCreateRequest;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatResponse;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatUpdateRequest;
import com.pro.project01.v2.domain.scorestat.entity.ScoreStat;
import com.pro.project01.v2.domain.scorestat.repository.ScoreStatRepository;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScoreStatServiceImpl implements ScoreStatService {

    private final ScoreStatRepository repo;
    private final UserRepository userRepo;
    private final SubjectRepository subjectRepo;
    private final RoundRepository roundRepo;

    /* ========= 조회 ========= */

    @Transactional(readOnly = true)
    @Override
    public List<ScoreStatResponse> findAll() {
        return repo.findAll().stream()
                .map(ScoreStatResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ScoreStatResponse> findByUserId(Long userId) {
        return repo.findByUser_Id(userId).stream()
                .map(ScoreStatResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ScoreStatResponse findById(Long id) {
        var stat = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("점수 통계를 찾을 수 없습니다."));
        return ScoreStatResponse.fromEntity(stat);
    }

    /* ========= 생성/수정/삭제 ========= */

    @Override
    public void create(ScoreStatCreateRequest request) {
        // 유니크 키(user,subject,round)에 이미 있으면 그 값을 덮어쓸지/에러로 볼지 정책 선택
        // 여기서는 upsert 느낌으로 덮어쓰기
        var existing = repo.findByUser_IdAndSubject_IdAndRound_Id(
                request.userId(), request.subjectId(), request.roundId()
        ).orElse(null);

        if (existing == null) {
            var stat = ScoreStat.builder()
                    .user(userRepo.getReferenceById(request.userId()))
                    .subject(subjectRepo.getReferenceById(request.subjectId()))
                    .round(roundRepo.getReferenceById(request.roundId()))
                    .totalQuestions(nz(request.totalQuestions()))
                    .correctAnswers(nz(request.correctAnswers()))
                    .accuracy(calcAccuracy(nz(request.totalQuestions()), nz(request.correctAnswers())))
                    .build();
            repo.save(stat);
        } else {
            existing.updateScore(nz(request.totalQuestions()), nz(request.correctAnswers()));
            // 엔티티의 updateScore가 updatedAt만 건드리는 경우 정답률 재계산 보강
            existing.setAccuracy(calcAccuracy(existing.getTotalQuestions(), existing.getCorrectAnswers()));
            // JPA dirty checking
        }
    }

    @Override
    public void update(Long id, ScoreStatUpdateRequest request) {
        var stat = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("점수 통계를 찾을 수 없습니다."));
        stat.updateScore(nz(request.totalQuestions()), nz(request.correctAnswers()));
        stat.setAccuracy(calcAccuracy(stat.getTotalQuestions(), stat.getCorrectAnswers()));
        // 필요 시 과목/회차 변경 허용하려면 아래 주석 해제
        // if (request.subjectId() != null) stat.setSubject(subjectRepo.getReferenceById(request.subjectId()));
        // if (request.roundId() != null)   stat.setRound(roundRepo.getReferenceById(request.roundId()));
        // if (request.userId() != null)    stat.setUser(userRepo.getReferenceById(request.userId()));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    /* ========= 즉답 기록(record) ========= */

    @Override
    public void record(Long userId, Long subjectId, Long roundId, boolean correct) {
        var stat = repo.findByUser_IdAndSubject_IdAndRound_Id(userId, subjectId, roundId)
                .orElseGet(() -> repo.save(
                        ScoreStat.builder()
                                .user(userRepo.getReferenceById(userId))
                                .subject(subjectRepo.getReferenceById(subjectId))
                                .round(roundRepo.getReferenceById(roundId))
                                .totalQuestions(0)
                                .correctAnswers(0)
                                .accuracy(0.0)
                                .build()
                ));

        stat.record(correct); // 엔티티 메서드가 total/correct/accuracy(소수1자리) 갱신
        // dirty checking으로 자동 flush
    }

    /* ========= 유틸 ========= */

    private static int nz(Integer v) { return v == null ? 0 : v; }

    private static double calcAccuracy(int total, int correct) {
        if (total <= 0) return 0.0;
        return Math.round((correct * 1000.0) / total) / 10.0; // 소수 1자리
    }
}
