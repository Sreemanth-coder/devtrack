package com.devtrack.dto.github;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
public class GitHubActivityResponse {

    private int totalCommits;
    private int activeDays;

    private LocalDate firstActivity;
    private LocalDate lastActivity;

    private Map<LocalDate, Integer> commitsByDate;
}