package com.devtrack.dto.skill;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserSkillRequest(

        @NotNull(message = "skillId is required")
        Long skillId,

        /**
         * Optional. 0-100. Omit to track a skill purely on project evidence.
         */
        @Min(value = 0, message = "Self assessment must be between 0 and 100")
        @Max(value = 100, message = "Self assessment must be between 0 and 100")
        Integer selfAssessment
) {
}
