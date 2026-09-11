package com.devtrack.controller;

import com.devtrack.dto.github.GitHubProfileResponse;
import com.devtrack.dto.github.GitHubRepositoryResponse;
import com.devtrack.dto.github.GitHubSkillEvidenceResponse;
import com.devtrack.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.devtrack.dto.github.GitHubAnalyticsResponse;
import java.util.List;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping("/profile")
    public GitHubProfileResponse getProfile() {
        return gitHubService.getProfile();
    }

    @GetMapping("/repositories")
    public List<GitHubRepositoryResponse> getRepositories() {
        return gitHubService.getRepositories();
    }
    @GetMapping("/analytics")
    public GitHubAnalyticsResponse getAnalytics() {
        return gitHubService.getAnalytics();
    }
    @GetMapping("/skill-evidence")
    public List<GitHubSkillEvidenceResponse> getSkillEvidence() {
        return gitHubService.getSkillEvidence();
    }
}