package com.devtrack.controller;

import com.devtrack.dto.project.AttachSkillsRequest;
import com.devtrack.dto.project.ProjectRequest;
import com.devtrack.dto.project.ProjectResponse;
import com.devtrack.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectResponse> list() {
        return projectService.list();
    }

    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/skills")
    public ProjectResponse attachSkills(@PathVariable Long id, @Valid @RequestBody AttachSkillsRequest request) {
        return projectService.attachSkills(id, request);
    }

    @DeleteMapping("/{id}/skills/{skillId}")
    public ProjectResponse removeSkill(@PathVariable Long id, @PathVariable Long skillId) {
        return projectService.removeSkill(id, skillId);
    }
}
