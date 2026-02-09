package com.sparkLab.study.planner.dto.dailyPlan;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlanCreateRes {

    private Long dailyPlanId;
    private boolean created;
}
