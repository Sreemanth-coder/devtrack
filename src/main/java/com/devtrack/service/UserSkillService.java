package com.devtrack.service;

import com.devtrack.dto.skill.UpdateUserSkillRequest;
import com.devtrack.dto.skill.UserSkillRequest;
import com.devtrack.dto.skill.UserSkillResponse;
import com.devtrack.entity.Project;
import com.devtrack.entity.Skill;
import com.devtrack.entity.UserSkill;
import com.devtrack.exception.DuplicateResourceException;
import com.devtrack.exception.ResourceNotFoundException;
import com.devtrack.repository.ProjectRepository;
import com.devtrack.repository.SkillRepository;
import com.devtrack.repository.UserSkillRepository;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserProvider currentUserProvider;
    private final SkillStrengthCalculator skillStrengthCalculator;

    @Transactional
    public UserSkillResponse create(UserSkillRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();

        if (userSkillRepository.existsByUserIdAndSkillId(userId, request.skillId())) {
            throw new DuplicateResourceException(
                    "You're already tracking this skill - update it instead of creating a duplicate");
        }

        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> ResourceNotFoundException.of("Skill", request.skillId()));

        UserSkill userSkill = UserSkill.builder()
                .user(currentUserProvider.getCurrentUser())
                .skill(skill)
                .selfAssessment(request.selfAssessment())
                .build();

        UserSkill saved = userSkillRepository.save(userSkill);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserSkillResponse> list() {
        Long userId = currentUserProvider.getCurrentUserId();
        return userSkillRepository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserSkillResponse getById(Long id) {
        return toResponse(findOwnedOrThrow(id));
    }

    @Transactional
    public UserSkillResponse update(Long id, UpdateUserSkillRequest request) {
        UserSkill userSkill = findOwnedOrThrow(id);
        userSkill.setSelfAssessment(request.selfAssessment());
        return toResponse(userSkillRepository.save(userSkill));
    }

    @Transactional
    public void delete(Long id) {
        UserSkill userSkill = findOwnedOrThrow(id);
        userSkillRepository.delete(userSkill);
    }

    private UserSkill findOwnedOrThrow(Long id) {
        Long userId = currentUserProvider.getCurrentUserId();
        return userSkillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("UserSkill", id));
    }

    private UserSkillResponse toResponse(UserSkill userSkill) {
        Long userId = userSkill.getUser().getId();
        Long skillId = userSkill.getSkill().getId();

        List<Project> linkedProjects = projectRepository.findByUserIdAndSkillId(userId, skillId);
        SkillStrengthCalculator.Result result =
                skillStrengthCalculator.calculate(linkedProjects, userSkill.getSelfAssessment());

        return UserSkillResponse.from(userSkill, result);
    }
}
