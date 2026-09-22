package com.devtrack.service;

import com.devtrack.dto.career.CareerAnalyticsResponse;
import com.devtrack.dto.dsa.DsaStatsResponse;
import com.devtrack.dto.github.GitHubActivityResponse;
import com.devtrack.dto.github.GitHubAnalyticsResponse;
import com.devtrack.dto.leetcode.LeetCodeStatsResponse;
import com.devtrack.entity.Project;
import com.devtrack.entity.ProjectStatus;
import com.devtrack.entity.UserSkill;
import com.devtrack.repository.ProjectRepository;
import com.devtrack.repository.UserSkillRepository;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerAnalyticsService {

    private final CurrentUserProvider currentUserProvider;
    private final DsaStatsService dsaStatsService;
    private final LeetCodeService leetCodeService;
    private final GitHubService gitHubService;
    private final UserSkillRepository userSkillRepository;
    private final ProjectRepository projectRepository;
    private final SkillStrengthCalculator skillStrengthCalculator;

    @Transactional(readOnly = true)
    public CareerAnalyticsResponse getAnalytics() {

        Long userId = currentUserProvider.getCurrentUserId();

        // -------------------------
        // DSA
        // -------------------------

        DsaStatsResponse dsaStats =
                dsaStatsService.getStats();

        LeetCodeStatsResponse leetCodeStats =
                leetCodeService.getStats();

        int dsaScore = calculateDsaScore(
                dsaStats,
                leetCodeStats
        );

        // -------------------------
        // Skills
        // -------------------------

        List<UserSkill> userSkills =
                userSkillRepository.findAllByUserId(userId);

        int totalSkills = userSkills.size();

        int averageStrength = 0;

        if (!userSkills.isEmpty()) {

            int totalStrength = 0;

            for (UserSkill userSkill : userSkills) {

                List<Project> linkedProjects =
                        projectRepository.findByUserIdAndSkillId(
                                userId,
                                userSkill.getSkill().getId()
                        );

                SkillStrengthCalculator.Result result =
                        skillStrengthCalculator.calculate(
                                linkedProjects,
                                userSkill.getSelfAssessment()
                        );

                totalStrength += result.strength();
            }

            averageStrength =
                    Math.round((float) totalStrength / totalSkills);
        }

        int skillsScore =
                Math.min(averageStrength, 100);

        // -------------------------
        // Projects
        // -------------------------

        List<Project> projects =
                projectRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        int totalProjects = projects.size();

        int completedProjects = 0;
        int inProgressProjects = 0;

        for (Project project : projects) {

            if (project.getStatus() == ProjectStatus.COMPLETED) {
                completedProjects++;
            }

            if (project.getStatus() == ProjectStatus.IN_PROGRESS) {
                inProgressProjects++;
            }
        }

        int projectsScore =
                calculateProjectsScore(
                        totalProjects,
                        completedProjects
                );

        // -------------------------
        // GitHub
        // -------------------------

        GitHubAnalyticsResponse githubAnalytics =
                gitHubService.getAnalytics();

        GitHubActivityResponse githubActivity =
                gitHubService.getActivity();

        int githubScore =
                calculateGitHubScore(
                        githubAnalytics,
                        githubActivity
                );

        // -------------------------
        // Consistency
        // -------------------------

        int consistencyScore =
                calculateConsistencyScore(
                        dsaStats,
                        githubActivity
                );

        // -------------------------
        // Final Career Score
        // -------------------------

        int careerScore =
                Math.round(
                        dsaScore * 0.30f
                                + skillsScore * 0.20f
                                + projectsScore * 0.20f
                                + githubScore * 0.20f
                                + consistencyScore * 0.10f
                );

        // -------------------------
        // Response
        // -------------------------

        return CareerAnalyticsResponse.builder()
                .careerScore(careerScore)

                .dsaScore(dsaScore)
                .skillsScore(skillsScore)
                .projectsScore(projectsScore)
                .githubScore(githubScore)
                .consistencyScore(consistencyScore)

                .dsa(
                        CareerAnalyticsResponse.DsaSummary.builder()
                                .devTrackSolved(dsaStats.totalSolved())
                                .leetCodeSolved(leetCodeStats.getTotalSolved())
                                .easySolved(leetCodeStats.getEasySolved())
                                .mediumSolved(leetCodeStats.getMediumSolved())
                                .hardSolved(leetCodeStats.getHardSolved())
                                .currentStreak(dsaStats.currentStreak())
                                .longestStreak(dsaStats.longestStreak())
                                .build()
                )

                .skills(
                        CareerAnalyticsResponse.SkillsSummary.builder()
                                .totalSkills(totalSkills)
                                .averageStrength(averageStrength)
                                .build()
                )

                .projects(
                        CareerAnalyticsResponse.ProjectsSummary.builder()
                                .totalProjects(totalProjects)
                                .completedProjects(completedProjects)
                                .inProgressProjects(inProgressProjects)
                                .build()
                )

                .github(
                        CareerAnalyticsResponse.GitHubSummary.builder()
                                .repositories(
                                        githubAnalytics.getTotalRepositories()
                                )
                                .originalRepositories(
                                        githubAnalytics.getOriginalRepositories()
                                )
                                .totalStars(
                                        githubAnalytics.getTotalStars()
                                )
                                .totalForks(
                                        githubAnalytics.getTotalForks()
                                )
                                .activeDays(
                                        githubActivity.getActiveDays()
                                )
                                .totalCommits(
                                        githubActivity.getTotalCommits()
                                )
                                .build()
                )

                .consistency(
                        CareerAnalyticsResponse.ConsistencySummary.builder()
                                .dsaCurrentStreak(
                                        dsaStats.currentStreak()
                                )
                                .dsaLongestStreak(
                                        dsaStats.longestStreak()
                                )
                                .githubActiveDays(
                                        githubActivity.getActiveDays()
                                )
                                .build()
                )

                .build();
    }

    // =========================================================
    // DSA SCORE — 30% of Career Score
    // =========================================================

    private int calculateDsaScore(
            DsaStatsResponse dsa,
            LeetCodeStatsResponse leetCode
    ) {

        int devTrackSolved =
                Math.min(dsa.totalSolved(), 100);

        int leetCodeSolved =
                Math.min(leetCode.getTotalSolved(), 200);

        double devTrackComponent =
                devTrackSolved * 0.30;

        double leetCodeComponent =
                (leetCodeSolved / 200.0) * 100 * 0.50;

        double difficultyComponent =
                calculateDifficultyComponent(leetCode);

        double score =
                devTrackComponent
                        + leetCodeComponent
                        + difficultyComponent;

        return Math.min(
                (int) Math.round(score),
                100
        );
    }

    private double calculateDifficultyComponent(
            LeetCodeStatsResponse stats
    ) {

        int medium =
                Math.min(stats.getMediumSolved(), 100);

        int hard =
                Math.min(stats.getHardSolved(), 50);

        double mediumScore =
                (medium / 100.0) * 10;

        double hardScore =
                (hard / 50.0) * 10;

        return mediumScore + hardScore;
    }

    // =========================================================
    // PROJECT SCORE — 20% of Career Score
    // =========================================================

    private int calculateProjectsScore(
            int totalProjects,
            int completedProjects
    ) {

        if (totalProjects == 0) {
            return 0;
        }

        int projectCountScore =
                Math.min(
                        totalProjects * 20,
                        60
                );

        int completionScore =
                Math.round(
                        ((float) completedProjects / totalProjects) * 40
                );

        return Math.min(
                projectCountScore + completionScore,
                100
        );
    }

    // =========================================================
    // GITHUB SCORE — 20% of Career Score
    // =========================================================

    private int calculateGitHubScore(
            GitHubAnalyticsResponse analytics,
            GitHubActivityResponse activity
    ) {

        int repositoryScore =
                Math.min(
                        analytics.getOriginalRepositories() * 10,
                        40
                );

        int starScore =
                Math.min(
                        analytics.getTotalStars() * 5,
                        20
                );

        int activityScore =
                Math.min(
                        activity.getActiveDays(),
                        40
                );

        return Math.min(
                repositoryScore
                        + starScore
                        + activityScore,
                100
        );
    }

    // =========================================================
    // CONSISTENCY SCORE — 10% of Career Score
    // =========================================================

    private int calculateConsistencyScore(
            DsaStatsResponse dsa,
            GitHubActivityResponse github
    ) {

        int streakScore =
                Math.min(
                        dsa.longestStreak() * 2,
                        40
                );

        int currentStreakScore =
                Math.min(
                        dsa.currentStreak() * 2,
                        30
                );

        int githubActivityScore =
                Math.min(
                        github.getActiveDays(),
                        30
                );

        return Math.min(
                streakScore
                        + currentStreakScore
                        + githubActivityScore,
                100
        );
    }
}