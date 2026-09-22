package com.devtrack.dto.dsa;

import java.util.List;

public record DsaActivityResponse(
        List<DayEntry> heatmap,
        List<PeriodEntry> weekly,
        List<PeriodEntry> monthly
) {
    /** One cell of the heatmap: an ISO date string and its solved count. */
    public record DayEntry(String date, int count) {
    }

    /** A rolled-up bucket: e.g. "2026-W34" or "2026-08" with a total count. */
    public record PeriodEntry(String period, int count) {
    }
}
