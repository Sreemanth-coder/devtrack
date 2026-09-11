package com.devtrack.controller;

import com.devtrack.dto.skill.SkillRequest;
import com.devtrack.dto.skill.SkillResponse;
import com.devtrack.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public List<SkillResponse> list() {
        return skillService.list();
    }

    @GetMapping("/{id}")
    public SkillResponse getById(@PathVariable Long id) {
        return skillService.getById(id);
    }

    @PostMapping
    public ResponseEntity<SkillResponse> create(@Valid @RequestBody SkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createCustom(request));
    }

    @PutMapping("/{id}")
    public SkillResponse update(@PathVariable Long id, @Valid @RequestBody SkillRequest request) {
        return skillService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
