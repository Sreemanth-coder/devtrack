package com.devtrack.dto.project;

import com.devtrack.dto.skill.SkillResponse;
import com.devtrack.entity.Project;
import com.devtrack.entity.ProjectStatus;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        ProjectStatus status,
        String githubUrl,
        String liveUrl,
        String imageUrl,
        String keyFeatures,
        String challenges,
        String learnings,
        String resumeDescription,
        String interviewNotes,
        List<SkillResponse> skills,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectResponse from(Project project) {
        List<SkillResponse> skills = project.getSkills().stream()
                .map(SkillResponse::from)
                .sorted(Comparator.comparing(SkillResponse::name))
                .toList();

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getGithubUrl(),
                project.getLiveUrl(),
                project.getImageUrl(),
                project.getKeyFeatures(),
                project.getChallenges(),
                project.getLearnings(),
                project.getResumeDescription(),
                project.getInterviewNotes(),
                skills,
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
