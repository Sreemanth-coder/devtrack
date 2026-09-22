package com.devtrack.repository;

import com.devtrack.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    /**
     * The catalog visible to a given user: every predefined/global skill,
     * plus that user's own custom skills (not other users' custom skills).
     */
    @Query("""
            select s from Skill s
            where s.custom = false or s.createdBy.id = :userId
            order by s.name asc
            """)
    List<Skill> findVisibleToUser(@Param("userId") Long userId);
}
