package com.devtrack.dto.skill;

import com.devtrack.entity.Skill;
import com.devtrack.entity.SkillCategory;

public record SkillResponse(
        Long id,
        String name,
        SkillCategory category,
        boolean custom
) {
    public static SkillResponse from(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getName(), skill.getCategory(), skill.isCustom());
    }
}
