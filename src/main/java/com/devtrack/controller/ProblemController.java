package com.devtrack.controller;

import com.devtrack.dto.common.PageResponse;
import com.devtrack.dto.dsa.ProblemRequest;
import com.devtrack.dto.dsa.ProblemResponse;
import com.devtrack.entity.Difficulty;
import com.devtrack.entity.ProblemStatus;
import com.devtrack.entity.ProgrammingLanguage;
import com.devtrack.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public PageResponse<ProblemResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) ProblemStatus status,
            @RequestParam(required = false) ProgrammingLanguage language,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return problemService.search(search, topic, difficulty, status, language, pageable);
    }

    @GetMapping("/{id}")
    public ProblemResponse getById(@PathVariable Long id) {
        return problemService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ProblemResponse> create(@Valid @RequestBody ProblemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(problemService.create(request));
    }

    @PutMapping("/{id}")
    public ProblemResponse update(@PathVariable Long id, @Valid @RequestBody ProblemRequest request) {
        return problemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        problemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
