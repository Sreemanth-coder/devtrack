package com.devtrack.dto.career;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CareerAnalyticsResponse {

    private Integer careerScore;

    private Integer dsaScore;
    private Integer skillsScore;
    private Integer projectsScore;
    private Integer githubScore;
    private Integer consistencyScore;

    private DsaSummary dsa;
    private SkillsSummary skills;
    private ProjectsSummary projects;
    private GitHubSummary github;
    private ConsistencySummary consistency;

    @Getter
    @Builder
    public static class DsaSummary {
        private Integer devTrackSolved;
        private Integer leetCodeSolved;
        private Integer easySolved;
        private Integer mediumSolved;
        private Integer hardSolved;
        private Integer currentStreak;
        private Integer longestStreak;
    }

    @Getter
    @Builder
    public static class SkillsSummary {
        private Integer totalSkills;
        private Integer averageStrength;
    }

    @Getter
    @Builder
    public static class ProjectsSummary {
        private Integer totalProjects;
        private Integer completedProjects;
        private Integer inProgressProjects;
    }

    @Getter
    @Builder
    public static class GitHubSummary {
        private Integer repositories;
        private Integer originalRepositories;
        private Integer totalStars;
        private Integer totalForks;
        private Integer activeDays;
        private Integer totalCommits;
    }

    @Getter
    @Builder
    public static class ConsistencySummary {
        private Integer dsaCurrentStreak;
        private Integer dsaLongestStreak;
        private Integer githubActiveDays;
    }
}