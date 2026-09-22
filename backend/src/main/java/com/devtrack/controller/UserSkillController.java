package com.devtrack.controller;

import com.devtrack.dto.skill.UpdateUserSkillRequest;
import com.devtrack.dto.skill.UserSkillRequest;
import com.devtrack.dto.skill.UserSkillResponse;
import com.devtrack.service.UserSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-skills")
@RequiredArgsConstructor
public class UserSkillController {

    private final UserSkillService userSkillService;

    @GetMapping
    public List<UserSkillResponse> list() {
        return userSkillService.list();
    }

    @GetMapping("/{id}")
    public UserSkillResponse getById(@PathVariable Long id) {
        return userSkillService.getById(id);
    }

    @PostMapping
    public ResponseEntity<UserSkillResponse> create(@Valid @RequestBody UserSkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userSkillService.create(request));
    }

    @PutMapping("/{id}")
    public UserSkillResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserSkillRequest request) {
        return userSkillService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userSkillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
