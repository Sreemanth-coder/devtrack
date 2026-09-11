package com.devtrack.dto.github;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GitHubSkillEvidenceResponse {

    private Long skillId;
    private String skillName;

    private int repositoryCount;
    private int totalStars;
    private int totalForks;

    private String evidenceLevel;
}