package com.devtrack.dto.skill;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateUserSkillRequest(

        @Min(value = 0, message = "Self assessment must be between 0 and 100")
        @Max(value = 100, message = "Self assessment must be between 0 and 100")
        Integer selfAssessment
) {
}
