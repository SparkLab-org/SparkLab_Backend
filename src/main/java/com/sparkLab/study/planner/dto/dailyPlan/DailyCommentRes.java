package com.sparkLab.study.planner.dto.dailyPlan;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyCommentRes {

    private Long dailyPlanId;
    private String comment;
}
