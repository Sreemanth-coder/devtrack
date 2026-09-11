package com.devtrack.github;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GitHubRepository {

    private Long id;
    private String name;
    private String description;

    private String html_url;
    private String language;

    private Integer stargazers_count;
    private Integer forks_count;

    private Boolean fork;

    private LocalDateTime updated_at;
}