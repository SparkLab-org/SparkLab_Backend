package com.sparkLab.study.planner.dto.dailyPlan;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DailyPlanRes {

    private Long dailyPlanId;
    private LocalDate planDate;
    private String comment;

    private List<Long> todos;
    private List<Long> routines;
}

