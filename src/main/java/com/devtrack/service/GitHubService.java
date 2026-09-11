package com.devtrack.service;

import com.devtrack.dto.github.GitHubAnalyticsResponse;
import com.devtrack.dto.github.GitHubProfileResponse;
import com.devtrack.dto.github.GitHubRepositoryResponse;
import com.devtrack.github.GitHubApiClient;
import com.devtrack.github.GitHubProfile;
import com.devtrack.github.GitHubRepository;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import com.devtrack.dto.github.GitHubSkillEvidenceResponse;
import com.devtrack.entity.Skill;
import com.devtrack.repository.SkillRepository;



@Service
@RequiredArgsConstructor
public class GitHubService {

    private final GitHubApiClient gitHubApiClient;
    private final CurrentUserProvider currentUserProvider;
    private final SkillRepository skillRepository;

    public GitHubProfileResponse getProfile() {

        String username = getGitHubUsername();

        GitHubProfile profile =
                gitHubApiClient.getProfile(username);

        return GitHubProfileResponse.builder()
                .username(profile.getLogin())
                .name(profile.getName())
                .avatarUrl(profile.getAvatar_url())
                .bio(profile.getBio())
                .publicRepositories(profile.getPublic_repos())
                .followers(profile.getFollowers())
                .following(profile.getFollowing())
                .profileUrl(profile.getHtml_url())
                .build();
    }

    public List<GitHubRepositoryResponse> getRepositories() {

        String username = getGitHubUsername();

        GitHubRepository[] repositories =
                gitHubApiClient.getRepositories(username);

        return Arrays.stream(repositories)
                .map(repo -> GitHubRepositoryResponse.builder()
                        .id(repo.getId())
                        .name(repo.getName())
                        .description(repo.getDescription())
                        .htmlUrl(repo.getHtml_url())
                        .language(repo.getLanguage())
                        .stars(repo.getStargazers_count())
                        .forks(repo.getForks_count())
                        .fork(repo.getFork())
                        .updatedAt(repo.getUpdated_at())
                        .build())
                .toList();
    }

    public GitHubAnalyticsResponse getAnalytics() {

        GitHubRepository[] repositories =
                gitHubApiClient.getRepositories(getGitHubUsername());

        int totalStars = 0;
        int totalForks = 0;
        int original = 0;
        int forks = 0;

        Map<String, Integer> languages = new java.util.HashMap<>();

        for (GitHubRepository repo : repositories) {

            totalStars += repo.getStargazers_count() != null
                    ? repo.getStargazers_count() : 0;

            totalForks += repo.getForks_count() != null
                    ? repo.getForks_count() : 0;

            if (Boolean.TRUE.equals(repo.getFork())) {
                forks++;
            } else {
                original++;
            }

            if (repo.getLanguage() != null) {
                languages.merge(repo.getLanguage(), 1, Integer::sum);
            }
        }

        String mostUsedLanguage = languages.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return GitHubAnalyticsResponse.builder()
                .totalRepositories(repositories.length)
                .originalRepositories(original)
                .forkedRepositories(forks)
                .totalStars(totalStars)
                .totalForks(totalForks)
                .mostUsedLanguage(mostUsedLanguage)
                .languageDistribution(languages)
                .build();
    }
    public List<GitHubSkillEvidenceResponse> getSkillEvidence() {

        GitHubRepository[] repositories =
                gitHubApiClient.getRepositories(getGitHubUsername());

        Map<String, GitHubSkillData> languageData = new HashMap<>();

        for (GitHubRepository repo : repositories) {

            String language = repo.getLanguage();

            if (language == null || language.isBlank()) {
                continue;
            }

            GitHubSkillData data = languageData.computeIfAbsent(
                    language,
                    key -> new GitHubSkillData()
            );

            data.repositoryCount++;

            data.totalStars += repo.getStargazers_count() != null
                    ? repo.getStargazers_count()
                    : 0;

            data.totalForks += repo.getForks_count() != null
                    ? repo.getForks_count()
                    : 0;
        }

        List<GitHubSkillEvidenceResponse> result = new ArrayList<>();

        List<Skill> skills = skillRepository.findAll();

        for (Skill skill : skills) {

            GitHubSkillData data =
                    languageData.get(skill.getName());

            int repositoryCount =
                    data != null ? data.repositoryCount : 0;

            int totalStars =
                    data != null ? data.totalStars : 0;

            int totalForks =
                    data != null ? data.totalForks : 0;

            String evidenceLevel;

            if (repositoryCount == 0) {
                evidenceLevel = "NO_EVIDENCE";
            } else if (repositoryCount == 1) {
                evidenceLevel = "BEGINNER";
            } else if (repositoryCount <= 3) {
                evidenceLevel = "DEVELOPING";
            } else {
                evidenceLevel = "STRONG";
            }

            result.add(
                    GitHubSkillEvidenceResponse.builder()
                            .skillId(skill.getId())
                            .skillName(skill.getName())
                            .repositoryCount(repositoryCount)
                            .totalStars(totalStars)
                            .totalForks(totalForks)
                            .evidenceLevel(evidenceLevel)
                            .build()
            );
        }

        return result;
    }

    private String getGitHubUsername() {

        String username =
                currentUserProvider.getCurrentUser()
                        .getGithubUsername();

        if (username == null || username.isBlank()) {
            throw new IllegalStateException(
                    "GitHub username is not configured for this user"
            );
        }

        return username;
    }
    private static class GitHubSkillData {

        int repositoryCount;
        int totalStars;
        int totalForks;
    }
}