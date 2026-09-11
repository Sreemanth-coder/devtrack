package com.devtrack.dto.github;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GitHubRepositoryResponse {

    private Long id;
    private String name;
    private String description;

    private String htmlUrl;

    private String language;

    private Integer stars;
    private Integer forks;

    private Boolean fork;

    private LocalDateTime updatedAt;
}