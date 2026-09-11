package com.devtrack.dto.dsa;

import com.devtrack.entity.Difficulty;
import com.devtrack.entity.Problem;
import com.devtrack.entity.ProblemStatus;
import com.devtrack.entity.ProgrammingLanguage;

import java.time.Instant;
import java.time.LocalDate;

public record ProblemResponse(
        Long id,
        String title,
        String topic,
        Difficulty difficulty,
        String platform,
        ProgrammingLanguage programmingLanguage,
        String problemUrl,
        ProblemStatus status,
        LocalDate solvedAt,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProblemResponse from(Problem problem) {
        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getTopic(),
                problem.getDifficulty(),
                problem.getPlatform(),
                problem.getProgrammingLanguage(),
                problem.getProblemUrl(),
                problem.getStatus(),
                problem.getSolvedAt(),
                problem.getNotes(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }
}
