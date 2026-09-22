package com.devtrack.dto.dsa;

public record StreakResponse(
        int currentStreak,
        int longestStreak,
        String lastActiveDate
) {
}
