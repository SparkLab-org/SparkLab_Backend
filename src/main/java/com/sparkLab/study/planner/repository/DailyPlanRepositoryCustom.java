package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.DailyPlan;
import java.time.LocalDate;
import java.util.List;

public interface DailyPlanRepositoryCustom {

    public List<DailyPlan> findByMenteeAndPlanDate(Long menteeId, LocalDate startDate, LocalDate endDate);
}
