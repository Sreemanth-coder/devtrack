package com.devtrack.github;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubApiClient {

    private final RestClient restClient;

    public GitHubApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }

    public GitHubProfile getProfile(String username) {
        return restClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GitHubProfile.class);
    }

    public GitHubRepository[] getRepositories(String username) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{username}/repos")
                        .queryParam("per_page", 100)
                        .queryParam("sort", "updated")
                        .build(username))
                .retrieve()
                .body(GitHubRepository[].class);
    }
}