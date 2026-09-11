package com.devtrack.repository;

import com.devtrack.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndUserId(Long id, Long userId);

    List<Project> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * All of this user's projects that reference the given skill, with
     * their status - exactly what SkillStrengthCalculator needs for
     * project evidence, without pulling in every project's full skill set.
     */
    @Query("""
            select p from Project p
            join p.skills s
            where p.user.id = :userId and s.id = :skillId
            """)
    List<Project> findByUserIdAndSkillId(@Param("userId") Long userId, @Param("skillId") Long skillId);

    @Query("""
            select count(p) from Project p
            join p.skills s
            where s.id = :skillId
            """)
    long countProjectsUsingSkill(@Param("skillId") Long skillId);
}
