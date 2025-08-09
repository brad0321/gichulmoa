package com.pro.project01.v2.domain.scorestat.service;

import com.pro.project01.v2.domain.round.entity.Round;
import com.pro.project01.v2.domain.round.repository.RoundRepository;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatCreateRequest;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatResponse;
import com.pro.project01.v2.domain.scorestat.dto.ScoreStatUpdateRequest;
import com.pro.project01.v2.domain.scorestat.entity.ScoreStat;
import com.pro.project01.v2.domain.scorestat.repository.ScoreStatRepository;
import com.pro.project01.v2.domain.subject.entity.Subject;
import com.pro.project01.v2.domain.subject.repository.SubjectRepository;

import com.pro.project01.v2.domain.user.entity.User;
import com.pro.project01.v2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreStatServiceImpl implements ScoreStatService {

    private final ScoreStatRepository scoreStatRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final RoundRepository roundRepository;

    @Override
    @Transactional
    public Long create(ScoreStatCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("과목 없음"));
        Round round = roundRepository.findById(request.roundId())
                .orElseThrow(() -> new IllegalArgumentException("회차 없음"));

        ScoreStat stat = ScoreStat.builder()
                .user(user)
                .subject(subject)
                .round(round)
                .totalQuestions(request.totalQuestions())
                .correctAnswers(request.correctAnswers())
                .build();

        return scoreStatRepository.save(stat).getId();
    }

    @Override
    @Transactional
    public void update(Long id, ScoreStatUpdateRequest request) {
        ScoreStat stat = scoreStatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("점수 기록 없음"));

        stat.updateScore(request.totalQuestions(), request.correctAnswers());
    }

    @Override
    public ScoreStatResponse findById(Long id) {
        return scoreStatRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("점수 기록 없음"));
    }

    @Override
    public List<ScoreStatResponse> findByUserId(Long userId) {
        return scoreStatRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ScoreStatResponse> findAll() {
        return scoreStatRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        scoreStatRepository.deleteById(id);
    }

    private ScoreStatResponse toResponse(ScoreStat stat) {
        return new ScoreStatResponse(
                stat.getId(),
                stat.getUser().getId(),
                stat.getSubject().getName(),
                stat.getRound().getRoundNumber(),
                stat.getTotalQuestions(),
                stat.getCorrectAnswers(),
                stat.getCreatedAt(),
                stat.getUpdatedAt()
        );
    }
}
