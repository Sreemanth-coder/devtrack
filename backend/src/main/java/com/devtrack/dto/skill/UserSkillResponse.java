package com.devtrack.dto.skill;

import com.devtrack.entity.UserSkill;
import com.devtrack.service.SkillStrengthCalculator;

import java.time.Instant;

public record UserSkillResponse(
        Long id,
        SkillResponse skill,
        Integer selfAssessment,
        int strength,
        Evidence evidence,
        Instant createdAt,
        Instant updatedAt
) {
    /**
     * The full, explainable breakdown behind `strength` - exactly what the
     * UI needs to show "Evidence: 3 projects, self-assessment 75%" next to
     * the score instead of presenting it as an opaque number.
     */
    public record Evidence(
            int projectCount,
            int projectEvidenceScore,
            Integer selfAssessment,
            String formula
    ) {
    }

    public static UserSkillResponse from(UserSkill userSkill, SkillStrengthCalculator.Result result) {
        String formula = result.selfAssessment() != null
                ? "60%% project evidence (%d/100) + 40%% self-assessment (%d/100)"
                        .formatted(result.projectEvidenceScore(), result.selfAssessment())
                : "100%% project evidence (%d/100) - no self-assessment set".formatted(result.projectEvidenceScore());

        Evidence evidence = new Evidence(
                result.projectCount(),
                result.projectEvidenceScore(),
                result.selfAssessment(),
                formula
        );

        return new UserSkillResponse(
                userSkill.getId(),
                SkillResponse.from(userSkill.getSkill()),
                userSkill.getSelfAssessment(),
                result.strength(),
                evidence,
                userSkill.getCreatedAt(),
                userSkill.getUpdatedAt()
        );
    }
}
