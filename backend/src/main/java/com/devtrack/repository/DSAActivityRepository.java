package com.devtrack.repository;

import com.devtrack.entity.DSAActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DSAActivityRepository extends JpaRepository<DSAActivity, Long> {

    Optional<DSAActivity> findByUserIdAndActivityDate(Long userId, LocalDate activityDate);

    List<DSAActivity> findAllByUserIdOrderByActivityDateAsc(Long userId);

    List<DSAActivity> findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(
            Long userId, LocalDate from, LocalDate to);
}
