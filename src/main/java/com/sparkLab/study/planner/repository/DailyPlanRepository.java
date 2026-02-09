package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.DailyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {

    @Query("SELECT d.dailyPlanId FROM DailyPlan d WHERE d.mentee.menteeId = :menteeId AND d.planDate = :planDate")
    Optional<Long> findIdByMenteeIdAndPlanDate(@Param("menteeId") Long menteeId,
                                               @Param("planDate") LocalDate planDate);

    Optional<DailyPlan> findByMentee_MenteeIdAndPlanDate(Long menteeMenteeId, LocalDate planDate);
}
