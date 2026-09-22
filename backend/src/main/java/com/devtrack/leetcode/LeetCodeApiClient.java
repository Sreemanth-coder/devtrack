package com.devtrack.leetcode;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class LeetCodeApiClient {

    private final RestClient restClient;

    public LeetCodeApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://leetcode.com")
                .defaultHeader("User-Agent", "DevTrack")
                .build();
    }

    public LeetCodeProfileResponse getUserProfile(String username) {

        String query = """
        query getUserProfile($username: String!) {
            matchedUser(username: $username) {
                username
                profile {
                    ranking
                    realName
                    aboutMe
                    userAvatar
                }
                submitStatsGlobal {
                    acSubmissionNum {
                        difficulty
                        count
                    }
                }
            }
        }
        """;

        Map<String, Object> request = Map.of(
                "query", query,
                "variables", Map.of("username", username)
        );

        return restClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(LeetCodeProfileResponse.class);
    }
}