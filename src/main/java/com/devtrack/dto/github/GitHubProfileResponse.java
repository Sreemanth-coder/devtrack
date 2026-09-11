package com.devtrack.dto.github;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GitHubProfileResponse {

    private String username;
    private String name;
    private String avatarUrl;
    private String bio;

    private Integer publicRepositories;
    private Integer followers;
    private Integer following;

    private String profileUrl;
}