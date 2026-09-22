package com.devtrack.controller;

import com.devtrack.dto.career.CareerAnalyticsResponse;
import com.devtrack.service.CareerAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/career")
@RequiredArgsConstructor
public class CareerAnalyticsController {

    private final CareerAnalyticsService careerAnalyticsService;

    @GetMapping("/analytics")
    public CareerAnalyticsResponse getAnalytics() {
        return careerAnalyticsService.getAnalytics();
    }
}