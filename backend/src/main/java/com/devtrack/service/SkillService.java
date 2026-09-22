package com.devtrack.service;

import com.devtrack.dto.skill.SkillRequest;
import com.devtrack.dto.skill.SkillResponse;
import com.devtrack.entity.Skill;
import com.devtrack.exception.DuplicateResourceException;
import com.devtrack.exception.ResourceNotFoundException;
import com.devtrack.repository.SkillRepository;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public List<SkillResponse> list() {
        Long userId = currentUserProvider.getCurrentUserId();
        return skillRepository.findVisibleToUser(userId).stream()
                .map(SkillResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SkillResponse getById(Long id) {
        return SkillResponse.from(findVisibleOrThrow(id));
    }

    /**
     * Creates a custom skill owned by the current user. Names are globally
     * unique (case-insensitive) across predefined and custom skills alike,
     * so this rejects anything that collides with an existing skill -
     * including someone else's custom one - rather than fragmenting
     * evidence for what's really the same skill under two rows.
     */
    @Transactional
    public SkillResponse createCustom(SkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A skill named '" + request.name() + "' already exists");
        }

        Skill skill = Skill.builder()
                .name(request.name())
                .category(request.category())
                .custom(true)
                .createdBy(currentUserProvider.getCurrentUser())
                .build();

        return SkillResponse.from(skillRepository.save(skill));
    }

    @Transactional
    public SkillResponse update(Long id, SkillRequest request) {
        Skill skill = findOwnedCustomOrThrow(id);

        if (!skill.getName().equalsIgnoreCase(request.name())
                && skillRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A skill named '" + request.name() + "' already exists");
        }

        skill.setName(request.name());
        skill.setCategory(request.category());

        return SkillResponse.from(skillRepository.save(skill));
    }

    @Transactional
    public void delete(Long id) {
        Skill skill = findOwnedCustomOrThrow(id);
        skillRepository.delete(skill);
    }

    private Skill findVisibleOrThrow(Long id) {
        Long userId = currentUserProvider.getCurrentUserId();
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Skill", id));

        boolean visible = !skill.isCustom() || (skill.getCreatedBy() != null && skill.getCreatedBy().getId().equals(userId));
        if (!visible) {
            throw ResourceNotFoundException.of("Skill", id);
        }
        return skill;
    }

    /**
     * Only a custom skill's own creator may edit/delete it. Predefined
     * skills are catalog data - nobody edits or deletes those through this
     * API. Both cases are reported as "not found" to avoid distinguishing
     * "exists but you can't touch it" from "doesn't exist".
     */
    private Skill findOwnedCustomOrThrow(Long id) {
        Long userId = currentUserProvider.getCurrentUserId();
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Skill", id));

        boolean ownedCustom = skill.isCustom() && skill.getCreatedBy() != null
                && skill.getCreatedBy().getId().equals(userId);
        if (!ownedCustom) {
            throw ResourceNotFoundException.of("Skill", id);
        }
        return skill;
    }
}
