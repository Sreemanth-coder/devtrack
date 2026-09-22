package com.devtrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_projects_user", columnList = "user_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Lob
    @Column(columnDefinition="TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNED;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "live_url", length = 500)
    private String liveUrl;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Lob
    @Column(name = "key_features", columnDefinition= "TEXT")
    private String keyFeatures;


    @Lob
    @Column(columnDefinition= "TEXT")
    private String challenges;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String learnings;

    @Column(name = "resume_description", length = 1000)
    private String resumeDescription;

    @Lob
    @Column(name = "interview_notes", columnDefinition = "TEXT")
    private String interviewNotes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Project N:M Skill. Unidirectional from Project - Skill doesn't need to
     * navigate back to every project that uses it via a mapped collection;
     * SkillStrengthCalculator queries the join table directly through
     * ProjectRepository instead, which is cheaper than lazily loading every
     * project's full skill set to compute one skill's evidence.
     *
     * Deliberately no cascade: a Skill attached here must always be an
     * existing, already-persisted Skill looked up via SkillRepository.
     * Cascading persist could silently create duplicate/rogue skill rows
     * from a stray transient object instead of surfacing that as a bug.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_skills",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();
}
