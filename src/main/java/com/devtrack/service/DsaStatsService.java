package com.devtrack.service;

import com.devtrack.dto.dsa.DsaStatsResponse;
import com.devtrack.entity.DSAActivity;
import com.devtrack.entity.Difficulty;
import com.devtrack.entity.ProblemStatus;
import com.devtrack.repository.ProblemRepository;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DsaStatsService {

    private final ProblemRepository problemRepository;
    private final DsaActivityService dsaActivityService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public DsaStatsResponse getStats() {
        Long userId = currentUserProvider.getCurrentUserId();

        long total = problemRepository.countByUserId(userId);
        long solved = problemRepository.countByUserIdAndStatus(userId, ProblemStatus.SOLVED);
        long todo = problemRepository.countByUserIdAndStatus(userId, ProblemStatus.TODO);
        long inProgress = problemRepository.countByUserIdAndStatus(userId, ProblemStatus.IN_PROGRESS);

        long easy = problemRepository.countByUserIdAndDifficulty(userId, Difficulty.EASY);
        long medium = problemRepository.countByUserIdAndDifficulty(userId, Difficulty.MEDIUM);
        long hard = problemRepository.countByUserIdAndDifficulty(userId, Difficulty.HARD);

        List<DsaStatsResponse.TopicProgress> topicProgress = problemRepository.countByTopicForUser(userId).stream()
                .map(p -> new DsaStatsResponse.TopicProgress(p.getTopic(), p.getTotal(), p.getSolved()))
                .toList();

        List<DSAActivity> activities = dsaActivityService.getAllActivity(userId);
        List<DsaStatsResponse.DailyCount> problemsOverTime = activities.stream()
                .map(a -> new DsaStatsResponse.DailyCount(a.getActivityDate().toString(), a.getSolvedCount()))
                .toList();

        DsaActivityService.StreakResult streaks = dsaActivityService.computeStreaks(userId);

        return new DsaStatsResponse(
                (int) total,
                (int) solved,
                (int) todo,
                (int) inProgress,
                new DsaStatsResponse.DifficultyBreakdown((int) easy, (int) medium, (int) hard),
                topicProgress,
                problemsOverTime,
                streaks.currentStreak(),
                streaks.longestStreak()
        );
    }
}
