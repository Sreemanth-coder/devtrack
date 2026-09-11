package com.devtrack.service;

import com.devtrack.entity.Project;
import com.devtrack.entity.ProjectStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Computes skill strength from evidence. This is the ONLY place the
 * formula is implemented - it is never stored, never manually editable,
 * and always recomputed from the current state of a user's projects and
 * self-assessment.
 *
 * <h2>The formula</h2>
 *
 * <b>Step 1 - Project Evidence Score (0-100):</b>
 * Every project the user has linked to this skill contributes points based
 * on how far along it is, because a finished project is stronger evidence
 * of a skill than one that's still planned:
 * <pre>
 *   COMPLETED    -> 1.0 points
 *   IN_PROGRESS  -> 0.6 points
 *   ARCHIVED     -> 0.5 points
 *   PLANNED      -> 0.3 points
 * </pre>
 * Those points are summed across all linked projects, then compared
 * against a cap equivalent to 3 completed projects (3.0 points = "you have
 * substantial, demonstrated evidence for this skill"):
 * <pre>
 *   projectEvidenceScore = min(totalPoints / 3.0, 1.0) * 100
 * </pre>
 * Example: 1 COMPLETED (1.0) + 1 IN_PROGRESS (0.6) = 1.6 points
 *          -> projectEvidenceScore = min(1.6/3.0, 1.0) * 100 = 53
 *
 * <b>Step 2 - Final Skill Strength (0-100):</b>
 * If the user has provided a self-assessment, it's blended in at a fixed,
 * minority weight - real project evidence counts for more than a claim
 * about yourself:
 * <pre>
 *   strength = round(0.6 * projectEvidenceScore + 0.4 * selfAssessment)
 * </pre>
 * If there's no self-assessment at all, the score is the project evidence
 * score on its own (there's nothing else to blend it with):
 * <pre>
 *   strength = round(projectEvidenceScore)
 * </pre>
 *
 * This deliberately means a self-assessment with zero supporting projects
 * is capped at 40 (0.4 * 100) - unverified self-report alone cannot claim
 * a high score. That's a design choice: strength should reflect
 * demonstrated evidence first, self-perception second.
 */
@Component
public class SkillStrengthCalculator {

    static final Map<ProjectStatus, Double> STATUS_WEIGHTS = Map.of(
            ProjectStatus.COMPLETED, 1.0,
            ProjectStatus.IN_PROGRESS, 0.6,
            ProjectStatus.ARCHIVED, 0.5,
            ProjectStatus.PLANNED, 0.3
    );

    static final double PROJECT_EVIDENCE_CAP_POINTS = 3.0;
    static final double PROJECT_EVIDENCE_WEIGHT = 0.6;
    static final double SELF_ASSESSMENT_WEIGHT = 0.4;

    public Result calculate(List<Project> linkedProjects, Integer selfAssessment) {
        double totalWeightedPoints = linkedProjects.stream()
                .mapToDouble(p -> STATUS_WEIGHTS.getOrDefault(p.getStatus(), 0.0))
                .sum();

        double projectEvidenceScore = Math.min(totalWeightedPoints / PROJECT_EVIDENCE_CAP_POINTS, 1.0) * 100;

        double finalStrength = selfAssessment != null
                ? (PROJECT_EVIDENCE_WEIGHT * projectEvidenceScore) + (SELF_ASSESSMENT_WEIGHT * selfAssessment)
                : projectEvidenceScore;

        return new Result(
                (int) Math.round(finalStrength),
                (int) Math.round(projectEvidenceScore),
                linkedProjects.size(),
                selfAssessment
        );
    }

    /**
     * @param strength            final 0-100 skill strength
     * @param projectEvidenceScore the 0-100 sub-score derived purely from projects, exposed so the UI can explain the breakdown
     * @param projectCount        how many of the user's projects use this skill
     * @param selfAssessment      the self-assessment used in the calculation, or null if none was set
     */
    public record Result(int strength, int projectEvidenceScore, int projectCount, Integer selfAssessment) {
    }
}
