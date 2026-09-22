package com.devtrack.dto.github;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class GitHubAnalyticsResponse {

    private int totalRepositories;
    private int originalRepositories;
    private int forkedRepositories;

    private int totalStars;
    private int totalForks;

    private String mostUsedLanguage;

    private Map<String, Integer> languageDistribution;
}