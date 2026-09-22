package com.devtrack.dto.leetcode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeetCodeStatsResponse {

    private String username;
    private Integer ranking;
    private Integer totalSolved;
    private Integer easySolved;
    private Integer mediumSolved;
    private Integer hardSolved;
}