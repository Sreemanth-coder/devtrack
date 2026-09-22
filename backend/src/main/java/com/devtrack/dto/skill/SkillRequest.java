package com.devtrack.dto.skill;

import com.devtrack.entity.SkillCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SkillRequest(

        @NotBlank(message = "Skill name is required")
        @Size(max = 100, message = "Skill name must be at most 100 characters")
        String name,

        @NotNull(message = "Category is required")
        SkillCategory category
) {
}
