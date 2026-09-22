package com.devtrack.github;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpStatusCodeException;

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

        try {
            return restClient.get()
                    .uri("/users/{username}", username)
                    .retrieve()
                    .body(GitHubProfile.class);

        } catch (HttpStatusCodeException e) {

            if (e.getStatusCode().value() == 403) {
                throw new GitHubApiException(
                        "GitHub API rate limit exceeded",
                        403,
                        e
                );
            }

            throw new GitHubApiException(
                    "GitHub API request failed",
                    e.getStatusCode().value(),
                    e
            );

        } catch (RestClientException e) {

            throw new GitHubApiException(
                    "Failed to connect to GitHub API",
                    502,
                    e
            );
        }
    }

    public GitHubRepository[] getRepositories(String username) {

        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users/{username}/repos")
                            .queryParam("per_page", 100)
                            .queryParam("sort", "updated")
                            .build(username))
                    .retrieve()
                    .body(GitHubRepository[].class);

        } catch (HttpStatusCodeException e) {

            if (e.getStatusCode().value() == 403) {
                throw new GitHubApiException(
                        "GitHub API rate limit exceeded",
                        403,
                        e
                );
            }

            throw new GitHubApiException(
                    "GitHub API request failed",
                    e.getStatusCode().value(),
                    e
            );

        } catch (RestClientException e) {

            throw new GitHubApiException(
                    "Failed to connect to GitHub API",
                    502,
                    e
            );
        }
    }

    public GitHubCommit[] getCommits(
            String username,
            String repository,
            String since,
            String until,
            int page
    ) {

        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/repos/{owner}/{repo}/commits")
                            .queryParam("author", username)
                            .queryParam("since", since)
                            .queryParam("until", until)
                            .queryParam("per_page", 100)
                            .queryParam("page", page)
                            .build(username, repository))
                    .retrieve()
                    .body(GitHubCommit[].class);

        } catch (HttpStatusCodeException e) {

            if (e.getStatusCode().value() == 403) {
                throw new GitHubApiException(
                        "GitHub API rate limit exceeded",
                        403,
                        e
                );
            }

            throw new GitHubApiException(
                    "GitHub API request failed",
                    e.getStatusCode().value(),
                    e
            );

        } catch (RestClientException e) {

            throw new GitHubApiException(
                    "Failed to connect to GitHub API",
                    502,
                    e
            );
        }
    }
}