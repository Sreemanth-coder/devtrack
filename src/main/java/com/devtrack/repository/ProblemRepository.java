package com.devtrack.repository;

import com.devtrack.entity.Difficulty;
import com.devtrack.entity.Problem;
import com.devtrack.entity.ProblemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long>, JpaSpecificationExecutor<Problem> {

    Optional<Problem> findByIdAndUserId(Long id, Long userId);

    List<Problem> findAllByUserId(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, ProblemStatus status);

    long countByUserIdAndDifficulty(Long userId, Difficulty difficulty);

    @Query("""
            select p.topic as topic,
                   count(p) as total,
                   sum(case when p.status = com.devtrack.entity.ProblemStatus.SOLVED then 1 else 0 end) as solved
            from Problem p
            where p.user.id = :userId
            group by p.topic
            order by p.topic asc
            """)
    List<TopicCountProjection> countByTopicForUser(@Param("userId") Long userId);

    interface TopicCountProjection {
        String getTopic();
        long getTotal();
        long getSolved();
    }
}
