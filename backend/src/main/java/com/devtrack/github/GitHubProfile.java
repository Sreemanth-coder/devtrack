package com.devtrack.github;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubProfile {

    private String login;
    private String name;
    private String avatar_url;
    private String bio;

    private Integer public_repos;
    private Integer followers;
    private Integer following;

    private String html_url;
}