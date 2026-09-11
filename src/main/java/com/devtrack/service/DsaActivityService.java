package com.devtrack.service;

import com.devtrack.dto.dsa.DsaActivityResponse;
import com.devtrack.entity.DSAActivity;
import com.devtrack.repository.DSAActivityRepository;
import com.devtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DsaActivityService {

    private final DSAActivityRepository activityRepository;
    private final UserRepository userRepository;

    /**
     * Increments the solved count for the given date, creating the row if
     * it doesn't exist yet.
     */
    @Transactional
    public void recordSolved(Long userId, LocalDate date) {
        if (date == null) {
            return;
        }
        DSAActivity activity = activityRepository.findByUserIdAndActivityDate(userId, date)
                .orElseGet(() -> DSAActivity.builder()
                        .user(userRepository.getReferenceById(userId))
                        .activityDate(date)
                        .solvedCount(0)
                        .build());
        activity.setSolvedCount(activity.getSolvedCount() + 1);
        activityRepository.save(activity);
    }

    /**
     * Decrements the solved count for the given date. If it reaches zero,
     * the row is deleted rather than kept at zero.
     */
    @Transactional
    public void recordUnsolved(Long userId, LocalDate date) {
        if (date == null) {
            return;
        }
        activityRepository.findByUserIdAndActivityDate(userId, date).ifPresent(activity -> {
            int updated = Math.max(0, activity.getSolvedCount() - 1);
            if (updated == 0) {
                activityRepository.delete(activity);
            } else {
                activity.setSolvedCount(updated);
                activityRepository.save(activity);
            }
        });
    }

    @Transactional(readOnly = true)
    public List<DSAActivity> getAllActivity(Long userId) {
        return activityRepository.findAllByUserIdOrderByActivityDateAsc(userId);
    }

    @Transactional(readOnly = true)
    public DsaActivityResponse getActivityView(Long userId) {
        List<DSAActivity> activities = getAllActivity(userId);

        List<DsaActivityResponse.DayEntry> heatmap = activities.stream()
                .map(a -> new DsaActivityResponse.DayEntry(a.getActivityDate().toString(), a.getSolvedCount()))
                .toList();

        Map<String, Integer> weeklyBuckets = new LinkedHashMap<>();
        Map<String, Integer> monthlyBuckets = new LinkedHashMap<>();

        for (DSAActivity a : activities) {
            LocalDate date = a.getActivityDate();
            String weekKey = date.getYear() + "-W" + String.format("%02d", date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
            String monthKey = date.getYear() + "-" + String.format("%02d", date.getMonthValue());

            weeklyBuckets.merge(weekKey, a.getSolvedCount(), Integer::sum);
            monthlyBuckets.merge(monthKey, a.getSolvedCount(), Integer::sum);
        }

        List<DsaActivityResponse.PeriodEntry> weekly = weeklyBuckets.entrySet().stream()
                .map(e -> new DsaActivityResponse.PeriodEntry(e.getKey(), e.getValue()))
                .toList();
        List<DsaActivityResponse.PeriodEntry> monthly = monthlyBuckets.entrySet().stream()
                .map(e -> new DsaActivityResponse.PeriodEntry(e.getKey(), e.getValue()))
                .toList();

        return new DsaActivityResponse(heatmap, weekly, monthly);
    }

    /**
     * Current streak: consecutive active days ending today or, if there's
     * no activity yet today, ending yesterday (so a streak isn't broken
     * just because the user hasn't solved anything yet today).
     * Longest streak: the longest run of consecutive active days ever.
     */
    @Transactional(readOnly = true)
    public StreakResult computeStreaks(Long userId) {
        List<DSAActivity> activities = getAllActivity(userId);
        if (activities.isEmpty()) {
            return new StreakResult(0, 0, null);
        }

        List<LocalDate> dates = activities.stream().map(DSAActivity::getActivityDate).sorted().toList();
        java.util.Set<LocalDate> dateSet = new java.util.HashSet<>(dates);

        // Longest streak: scan sorted dates, count consecutive runs.
        int longest = 1;
        int run = 1;
        for (int i = 1; i < dates.size(); i++) {
            if (ChronoUnit.DAYS.between(dates.get(i - 1), dates.get(i)) == 1) {
                run++;
            } else {
                run = 1;
            }
            longest = Math.max(longest, run);
        }

        // Current streak: walk backwards from today (or yesterday) while consecutive days exist.
        LocalDate today = LocalDate.now();
        LocalDate anchor = dateSet.contains(today) ? today : today.minusDays(1);
        int current = 0;
        LocalDate cursor = anchor;
        while (dateSet.contains(cursor)) {
            current++;
            cursor = cursor.minusDays(1);
        }

        LocalDate lastActive = dates.get(dates.size() - 1);
        return new StreakResult(current, longest, lastActive);
    }

    public record StreakResult(int currentStreak, int longestStreak, LocalDate lastActiveDate) {
    }
}
