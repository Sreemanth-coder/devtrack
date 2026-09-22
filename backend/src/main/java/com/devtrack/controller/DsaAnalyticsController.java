package com.devtrack.controller;

import com.devtrack.dto.dsa.DsaActivityResponse;
import com.devtrack.dto.dsa.DsaStatsResponse;
import com.devtrack.dto.dsa.StreakResponse;
import com.devtrack.service.DsaActivityService;
import com.devtrack.service.DsaStatsService;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dsa")
@RequiredArgsConstructor
public class DsaAnalyticsController {

    private final DsaStatsService dsaStatsService;
    private final DsaActivityService dsaActivityService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/stats")
    public DsaStatsResponse stats() {
        return dsaStatsService.getStats();
    }

    @GetMapping("/activity")
    public DsaActivityResponse activity() {
        return dsaActivityService.getActivityView(currentUserProvider.getCurrentUserId());
    }

    @GetMapping("/streak")
    public StreakResponse streak() {
        Long userId = currentUserProvider.getCurrentUserId();
        DsaActivityService.StreakResult result = dsaActivityService.computeStreaks(userId);
        String lastActive = result.lastActiveDate() != null ? result.lastActiveDate().toString() : null;
        return new StreakResponse(result.currentStreak(), result.longestStreak(), lastActive);
    }
}
