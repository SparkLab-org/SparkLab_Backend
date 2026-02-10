package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.ProgressStatics;
import com.sparkLab.study.user.entity.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyPlanSummaryRepository extends JpaRepository<ProgressStatics, Long> {

    Optional<ProgressStatics> findByMenteeAndPlanDate(Mentee mentee, LocalDate planDate);

    List<ProgressStatics> findByMenteeAndPlanDateBetweenOrderByPlanDateAsc(Mentee mentee, LocalDate start, LocalDate end);

    @Modifying
    @Query("""
    UPDATE ProgressStatics s
    SET s.totalCount = :totalCount, 
        s.completedCount = :completedCount, 
        s.achievementRate = :achievementRate
    WHERE s.mentee = :mentee AND s.planDate = :planDate
""")
    int updateSummary(Mentee mentee, LocalDate planDate, int totalCount, int completedCount, double achievementRate);

}
