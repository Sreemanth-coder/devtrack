package com.devtrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A user's tracked skill. Deliberately does NOT have a "strength" or
 * "score" column - that value is always computed on read by
 * SkillStrengthCalculator from project evidence + selfAssessment, so it
 * can never drift out of sync or be edited directly into an arbitrary
 * number. selfAssessment is the only value the user controls here.
 */
@Entity
@Table(name = "user_skills",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_skill", columnNames = {"user_id", "skill_id"}),
        indexes = @Index(name = "idx_user_skills_user", columnList = "user_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    /**
     * 0-100, user-entered, optional. Contributes at most 40% of the final
     * skill strength - see SkillStrengthCalculator for the full formula.
     */
    @Column(name = "self_assessment")
    private Integer selfAssessment;
}
