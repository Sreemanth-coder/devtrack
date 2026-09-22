package com.devtrack.service;

import com.devtrack.dto.project.AttachSkillsRequest;
import com.devtrack.dto.project.ProjectRequest;
import com.devtrack.dto.project.ProjectResponse;
import com.devtrack.entity.Project;
import com.devtrack.entity.Skill;
import com.devtrack.exception.ResourceNotFoundException;
import com.devtrack.repository.ProjectRepository;
import com.devtrack.repository.SkillRepository;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .status(request.status())
                .githubUrl(request.githubUrl())
                .liveUrl(request.liveUrl())
                .imageUrl(request.imageUrl())
                .keyFeatures(request.keyFeatures())
                .challenges(request.challenges())
                .learnings(request.learnings())
                .resumeDescription(request.resumeDescription())
                .interviewNotes(request.interviewNotes())
                .user(currentUserProvider.getCurrentUser())
                .build();

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> list() {
        Long userId = currentUserProvider.getCurrentUserId();
        return projectRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        return ProjectResponse.from(findOwnedOrThrow(id));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = findOwnedOrThrow(id);

        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(request.status());
        project.setGithubUrl(request.githubUrl());
        project.setLiveUrl(request.liveUrl());
        project.setImageUrl(request.imageUrl());
        project.setKeyFeatures(request.keyFeatures());
        project.setChallenges(request.challenges());
        project.setLearnings(request.learnings());
        project.setResumeDescription(request.resumeDescription());
        project.setInterviewNotes(request.interviewNotes());

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id) {
        Project project = findOwnedOrThrow(id);
        projectRepository.delete(project);
    }

    @Transactional
    public ProjectResponse attachSkills(Long id, AttachSkillsRequest request) {
        Project project = findOwnedOrThrow(id);

        for (Long skillId : request.skillIds()) {
            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Skill", skillId));
            project.getSkills().add(skill);
        }

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse removeSkill(Long id, Long skillId) {
        Project project = findOwnedOrThrow(id);
        project.getSkills().removeIf(skill -> skill.getId().equals(skillId));
        return ProjectResponse.from(projectRepository.save(project));
    }

    private Project findOwnedOrThrow(Long id) {
        Long userId = currentUserProvider.getCurrentUserId();
        return projectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", id));
    }
}
