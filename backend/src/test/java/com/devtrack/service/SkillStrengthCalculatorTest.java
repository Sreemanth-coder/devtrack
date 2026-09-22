package com.devtrack.service;

import com.devtrack.entity.Project;
import com.devtrack.entity.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SkillStrengthCalculatorTest {

    private final SkillStrengthCalculator calculator = new SkillStrengthCalculator();

    private Project projectWithStatus(ProjectStatus status) {
        return Project.builder().status(status).build();
    }

    @Test
    void noProjectsAndNoSelfAssessment_yieldsZero() {
        var result = calculator.calculate(List.of(), null);

        assertThat(result.strength()).isEqualTo(0);
        assertThat(result.projectEvidenceScore()).isEqualTo(0);
        assertThat(result.projectCount()).isEqualTo(0);
    }

    @Test
    void threeCompletedProjects_noSelfAssessment_yieldsMaxEvidenceScore() {
        // 3 x 1.0 = 3.0 points = the full cap -> 100
        var result = calculator.calculate(List.of(
                projectWithStatus(ProjectStatus.COMPLETED),
                projectWithStatus(ProjectStatus.COMPLETED),
                projectWithStatus(ProjectStatus.COMPLETED)
        ), null);

        assertThat(result.projectEvidenceScore()).isEqualTo(100);
        assertThat(result.strength()).isEqualTo(100); // no self-assessment -> strength == evidence score
    }

    @Test
    void oneCompletedOneInProgress_noSelfAssessment_matchesWorkedExample() {
        // 1.0 + 0.6 = 1.6 points -> min(1.6/3.0, 1) * 100 = 53.33... -> rounds to 53
        var result = calculator.calculate(List.of(
                projectWithStatus(ProjectStatus.COMPLETED),
                projectWithStatus(ProjectStatus.IN_PROGRESS)
        ), null);

        assertThat(result.projectEvidenceScore()).isEqualTo(53);
        assertThat(result.strength()).isEqualTo(53);
    }

    @Test
    void selfAssessmentAlone_withNoProjects_isCappedAt40PercentWeight() {
        // No projects -> evidence score 0. strength = 0.6*0 + 0.4*80 = 32
        var result = calculator.calculate(List.of(), 80);

        assertThat(result.projectEvidenceScore()).isEqualTo(0);
        assertThat(result.strength()).isEqualTo(32);
        assertThat(result.selfAssessment()).isEqualTo(80);
    }

    @Test
    void fullEvidencePlusSelfAssessment_blendsAtDocumentedWeights() {
        // 3 completed -> evidence 100. self-assessment 80.
        // strength = round(0.6*100 + 0.4*80) = round(60 + 32) = 92
        var result = calculator.calculate(List.of(
                projectWithStatus(ProjectStatus.COMPLETED),
                projectWithStatus(ProjectStatus.COMPLETED),
                projectWithStatus(ProjectStatus.COMPLETED)
        ), 80);

        assertThat(result.strength()).isEqualTo(92);
    }

    @Test
    void moreThanCapWorthOfProjects_doesNotExceed100EvidenceScore() {
        // 5 completed projects = 5.0 points, well past the 3.0 cap
        var result = calculator.calculate(List.of(
                projectWithStatus(ProjectStatus.COMPLETED),
                projectWithStatus(ProjectStatus.COMPLETED),
                projectWithStatus(ProjectStatus.COMPLETED),
                projectWithStatus(ProjectStatus.COMPLETED),
                projectWithStatus(ProjectStatus.COMPLETED)
        ), null);

        assertThat(result.projectEvidenceScore()).isEqualTo(100);
        assertThat(result.projectCount()).isEqualTo(5);
    }

    @Test
    void plannedAndArchivedProjects_contributeLessThanCompleted() {
        var plannedOnly = calculator.calculate(List.of(projectWithStatus(ProjectStatus.PLANNED)), null);
        var archivedOnly = calculator.calculate(List.of(projectWithStatus(ProjectStatus.ARCHIVED)), null);
        var completedOnly = calculator.calculate(List.of(projectWithStatus(ProjectStatus.COMPLETED)), null);

        assertThat(plannedOnly.projectEvidenceScore()).isLessThan(archivedOnly.projectEvidenceScore());
        assertThat(archivedOnly.projectEvidenceScore()).isLessThan(completedOnly.projectEvidenceScore());
    }
}
