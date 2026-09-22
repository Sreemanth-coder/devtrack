package com.devtrack.service;

import com.devtrack.leetcode.LeetCodeApiClient;
import com.devtrack.leetcode.LeetCodeProfileResponse;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.devtrack.dto.leetcode.LeetCodeStatsResponse;
import com.devtrack.leetcode.LeetCodeProfileResponse;
import com.devtrack.exception.BadRequestException;
@Service
@RequiredArgsConstructor
public class LeetCodeService {


    private final CurrentUserProvider currentUserProvider;
    private final LeetCodeApiClient leetCodeApiClient;




    public LeetCodeStatsResponse getStats() {

        String username = currentUserProvider
                .getCurrentUser()
                .getLeetcodeUsername();

        if (username == null || username.isBlank()) {
            throw new BadRequestException(
                    "LeetCode username is not configured"
            );
        }
        LeetCodeProfileResponse response =
                leetCodeApiClient.getUserProfile(username);

        if (response == null
                || response.getData() == null
                || response.getData().getMatchedUser() == null) {

            throw new BadRequestException("LeetCode username not found");
        }

        LeetCodeProfileResponse.MatchedUser user =
                response.getData().getMatchedUser();

        int totalSolved = 0;
        int easySolved = 0;
        int mediumSolved = 0;
        int hardSolved = 0;

        for (LeetCodeProfileResponse.SubmissionCount stat :
                user.getSubmitStatsGlobal().getAcSubmissionNum()) {

            switch (stat.getDifficulty()) {
                case "All" -> totalSolved = stat.getCount();
                case "Easy" -> easySolved = stat.getCount();
                case "Medium" -> mediumSolved = stat.getCount();
                case "Hard" -> hardSolved = stat.getCount();
            }
        }

        return LeetCodeStatsResponse.builder()
                .username(user.getUsername())
                .ranking(user.getProfile().getRanking())
                .totalSolved(totalSolved)
                .easySolved(easySolved)
                .mediumSolved(mediumSolved)
                .hardSolved(hardSolved)
                .build();
    }
}