package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.DailyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {

    @Query("SELECT d.dailyPlanId FROM DailyPlan d WHERE d.mentee.menteeId = :menteeId AND d.planDate = :planDate")
    List<Long> findIdByMenteeIdAndPlanDate(@Param("menteeId") Long menteeId,
                                          @Param("planDate") LocalDate planDate);

    List<DailyPlan> findByMentee_MenteeIdAndPlanDate(Long menteeMenteeId, LocalDate planDate);
}
