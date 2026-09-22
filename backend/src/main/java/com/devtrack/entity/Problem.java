package com.devtrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "problems", indexes = {
        @Index(name = "idx_problems_user", columnList = "user_id"),
        @Index(name = "idx_problems_user_status", columnList = "user_id, status"),
        @Index(name = "idx_problems_user_topic", columnList = "user_id, topic")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(length = 100)
    private String platform;

    @Enumerated(EnumType.STRING)
    @Column(name = "programming_language", length = 20)
    private ProgrammingLanguage programmingLanguage;

    @Column(name = "problem_url", length = 500)
    private String problemUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProblemStatus status = ProblemStatus.TODO;

    @Column(name = "solved_at")
    private LocalDate solvedAt;

    @Column(length = 2000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
