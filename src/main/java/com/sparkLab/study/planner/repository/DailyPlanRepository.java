package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.DailyPlan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {

    Optional<DailyPlan> findByMentee_MenteeIdAndPlanDate(Long menteeId, LocalDate planDate);
}
