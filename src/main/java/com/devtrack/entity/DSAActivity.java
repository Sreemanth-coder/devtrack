package com.devtrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.LocalDate;

/**
 * Daily aggregate of solved problems per user. Exactly one row per
 * (user, activityDate) — enforced by a unique constraint — so streak
 * and heatmap calculations only ever have to reason about a single
 * count per day instead of reconstructing it from raw Problem rows.
 *
 * A row is only ever created when solvedCount > 0; when a day's count
 * drops back to 0 (e.g. a problem un-solved or deleted) the row is
 * removed rather than kept at zero, which keeps "does this date have
 * activity" a simple existence check.
 */
@Entity
@Table(name = "dsa_activities", uniqueConstraints = @UniqueConstraint(
        name = "uk_dsa_activity_user_date", columnNames = {"user_id", "activity_date"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DSAActivity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "solved_count", nullable = false)
    @Builder.Default
    private int solvedCount = 0;
}
