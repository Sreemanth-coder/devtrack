package com.devtrack.dto.dsa;

import java.util.List;

public record DsaStatsResponse(
        int totalProblems,
        int totalSolved,
        int totalTodo,
        int totalInProgress,
        DifficultyBreakdown difficultyBreakdown,
        List<TopicProgress> topicProgress,
        List<DailyCount> problemsSolvedOverTime,
        int currentStreak,
        int longestStreak
) {
    /** Ready-made for a donut/pie chart. */
    public record DifficultyBreakdown(int easy, int medium, int hard) {
    }

    /** Ready-made for topic progress bars: solved/total per topic. */
    public record TopicProgress(String topic, long total, long solved) {
    }

    /** Ready-made for a problems-over-time line chart. */
    public record DailyCount(String date, int count) {
    }
}
