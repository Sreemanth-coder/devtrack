package com.devtrack.repository;

import com.devtrack.entity.Difficulty;
import com.devtrack.entity.Problem;
import com.devtrack.entity.ProblemStatus;
import com.devtrack.entity.ProgrammingLanguage;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ProblemSpecifications {

    private ProblemSpecifications() {
    }

    /**
     * Every query built from these specifications must include this,
     * scoping results to the authenticated user. This is the DB-level
     * enforcement of ownership, on top of the controller/service never
     * trusting a client-supplied userId.
     */
    public static Specification<Problem> belongsToUser(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Problem> titleContains(String search) {
        if (!StringUtils.hasText(search)) {
            return null;
        }
        String pattern = "%" + search.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern);
    }

    public static Specification<Problem> hasTopic(String topic) {
        if (!StringUtils.hasText(topic)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(cb.lower(root.get("topic")), topic.toLowerCase());
    }

    public static Specification<Problem> hasDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("difficulty"), difficulty);
    }

    public static Specification<Problem> hasStatus(ProblemStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Problem> hasLanguage(ProgrammingLanguage language) {
        if (language == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("programmingLanguage"), language);
    }
}
