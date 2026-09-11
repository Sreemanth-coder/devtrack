package com.devtrack.dto.project;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AttachSkillsRequest(
        @NotEmpty(message = "At least one skillId is required")
        List<Long> skillIds
) {
}
