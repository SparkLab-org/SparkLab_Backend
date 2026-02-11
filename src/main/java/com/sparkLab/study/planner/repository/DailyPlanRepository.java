package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.DailyPlan;
import com.sparkLab.study.user.entity.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;


public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long>{

    @Query("SELECT d.dailyPlanId FROM DailyPlan d WHERE d.mentee.menteeId = :menteeId AND d.planDate = :planDate")
    List<Long> findIdByMenteeIdAndPlanDate(@Param("menteeId") Long menteeId,
                                          @Param("planDate") LocalDate planDate);

    // ------------------ 배치용 summary ------------------
    @Query("""
    SELECT dp.planDate AS planDate,
           COUNT(t) AS totalCount,
           SUM(CASE WHEN t.completedAt != null THEN 1 ELSE 0 END) AS completedCount
    FROM DailyPlan dp
    LEFT JOIN dp.todoItems t
    WHERE dp.mentee.menteeId = :menteeId
      AND dp.planDate BETWEEN :startDate AND :endDate
    GROUP BY dp.planDate
    ORDER BY dp.planDate ASC
""")
    List<Object[]> findDailyTodoSummary(@Param("menteeId") Long menteeId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    // 모든 멘티 조회 (배치용)
    @Query("SELECT DISTINCT dp.mentee FROM DailyPlan dp")
    List<Mentee> findAllMenteesWithPlans();
}
