package com.devtrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * A skill in the catalog. Either predefined (isCustom = false, no owner,
 * visible to everyone) or custom (isCustom = true, owned by the user who
 * created it). Names are globally unique (case-insensitive) regardless of
 * who created the skill, so "Java" can't exist twice under two different
 * casings/owners and fragment evidence for what's really the same skill.
 */
@Entity
@Table(name = "skills", uniqueConstraints = @UniqueConstraint(name = "uk_skills_name", columnNames = "name"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SkillCategory category;

    @Column(name = "is_custom", nullable = false)
    @Builder.Default
    private boolean custom = false;

    /**
     * Only set when custom = true. Null for predefined/global skills.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;
}
