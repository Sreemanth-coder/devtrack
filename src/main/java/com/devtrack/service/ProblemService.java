package com.devtrack.service;

import com.devtrack.dto.common.PageResponse;
import com.devtrack.dto.dsa.ProblemRequest;
import com.devtrack.dto.dsa.ProblemResponse;
import com.devtrack.entity.Difficulty;
import com.devtrack.entity.Problem;
import com.devtrack.entity.ProblemStatus;
import com.devtrack.entity.ProgrammingLanguage;
import com.devtrack.exception.ResourceNotFoundException;
import com.devtrack.repository.ProblemRepository;
import com.devtrack.repository.ProblemSpecifications;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final CurrentUserProvider currentUserProvider;
    private final DsaActivityService dsaActivityService;

    @Transactional
    public ProblemResponse create(ProblemRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();

        Problem problem = Problem.builder()
                .title(request.title())
                .topic(request.topic())
                .difficulty(request.difficulty())
                .platform(request.platform())
                .programmingLanguage(request.programmingLanguage())
                .problemUrl(request.problemUrl())
                .status(request.status())
                .solvedAt(resolveSolvedAt(request.status(), request.solvedAt()))
                .notes(request.notes())
                .user(currentUserProvider.getCurrentUser())
                .build();

        Problem saved = problemRepository.save(problem);

        if (saved.getStatus() == ProblemStatus.SOLVED) {
            dsaActivityService.recordSolved(userId, saved.getSolvedAt());
        }

        return ProblemResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProblemResponse getById(Long id) {
        return ProblemResponse.from(findOwnedOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ProblemResponse> search(String search, String topic, Difficulty difficulty,
                                                 ProblemStatus status, ProgrammingLanguage language,
                                                 Pageable pageable) {
        Long userId = currentUserProvider.getCurrentUserId();

        Specification<Problem> spec = Specification.where(ProblemSpecifications.belongsToUser(userId));

        Specification<Problem> searchSpec = ProblemSpecifications.titleContains(search);
        if (searchSpec != null) spec = spec.and(searchSpec);

        Specification<Problem> topicSpec = ProblemSpecifications.hasTopic(topic);
        if (topicSpec != null) spec = spec.and(topicSpec);

        Specification<Problem> difficultySpec = ProblemSpecifications.hasDifficulty(difficulty);
        if (difficultySpec != null) spec = spec.and(difficultySpec);

        Specification<Problem> statusSpec = ProblemSpecifications.hasStatus(status);
        if (statusSpec != null) spec = spec.and(statusSpec);

        Specification<Problem> languageSpec = ProblemSpecifications.hasLanguage(language);
        if (languageSpec != null) spec = spec.and(languageSpec);

        Page<Problem> page = problemRepository.findAll(spec, pageable);
        return PageResponse.of(page, ProblemResponse::from);
    }

    @Transactional
    public ProblemResponse update(Long id, ProblemRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();
        Problem problem = findOwnedOrThrow(id);

        ProblemStatus previousStatus = problem.getStatus();
        LocalDate previousSolvedAt = problem.getSolvedAt();

        problem.setTitle(request.title());
        problem.setTopic(request.topic());
        problem.setDifficulty(request.difficulty());
        problem.setPlatform(request.platform());
        problem.setProgrammingLanguage(request.programmingLanguage());
        problem.setProblemUrl(request.problemUrl());
        problem.setStatus(request.status());
        problem.setNotes(request.notes());

        LocalDate newSolvedAt = resolveSolvedAt(request.status(), request.solvedAt());
        problem.setSolvedAt(newSolvedAt);

        Problem saved = problemRepository.save(problem);

        reconcileActivity(userId, previousStatus, previousSolvedAt, saved.getStatus(), saved.getSolvedAt());

        return ProblemResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Long userId = currentUserProvider.getCurrentUserId();
        Problem problem = findOwnedOrThrow(id);

        if (problem.getStatus() == ProblemStatus.SOLVED) {
            dsaActivityService.recordUnsolved(userId, problem.getSolvedAt());
        }

        problemRepository.delete(problem);
    }

    private Problem findOwnedOrThrow(Long id) {
        Long userId = currentUserProvider.getCurrentUserId();
        // Looking up by (id, userId) together means a request for another
        // user's problem behaves identically to a request for a problem
        // that doesn't exist at all - no existence leak.
        return problemRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Problem", id));
    }

    private LocalDate resolveSolvedAt(ProblemStatus status, LocalDate requestedSolvedAt) {
        if (status != ProblemStatus.SOLVED) {
            return null;
        }
        return requestedSolvedAt != null ? requestedSolvedAt : LocalDate.now();
    }

    /**
     * Keeps DSAActivity in sync whenever a problem's solved-state or solved
     * date changes on update.
     */
    private void reconcileActivity(Long userId, ProblemStatus oldStatus, LocalDate oldSolvedAt,
                                    ProblemStatus newStatus, LocalDate newSolvedAt) {
        boolean wasSolved = oldStatus == ProblemStatus.SOLVED;
        boolean isSolved = newStatus == ProblemStatus.SOLVED;

        if (wasSolved && !isSolved) {
            dsaActivityService.recordUnsolved(userId, oldSolvedAt);
        } else if (!wasSolved && isSolved) {
            dsaActivityService.recordSolved(userId, newSolvedAt);
        } else if (wasSolved && isSolved && !java.util.Objects.equals(oldSolvedAt, newSolvedAt)) {
            dsaActivityService.recordUnsolved(userId, oldSolvedAt);
            dsaActivityService.recordSolved(userId, newSolvedAt);
        }
        // if neither was/is solved, or both solved with same date, nothing to do
    }
}
