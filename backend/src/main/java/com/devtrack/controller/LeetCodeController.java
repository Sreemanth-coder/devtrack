package com.devtrack.controller;

import com.devtrack.dto.leetcode.LeetCodeStatsResponse;
import com.devtrack.service.LeetCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leetcode")
@RequiredArgsConstructor
public class LeetCodeController {

    private final LeetCodeService leetCodeService;

    @GetMapping("/stats")
    public LeetCodeStatsResponse getStats() {
        return leetCodeService.getStats();
    }
}