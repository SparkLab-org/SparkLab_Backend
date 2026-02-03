package com.sparkLab.study.repository;

import com.sparkLab.study.entity.Planner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PlannerRepository extends JpaRepository<Planner, Long> {

    Optional<Planner> findByMentee_MenteeIdAndPlanDate(Long menteeId, LocalDate planDate);
}
