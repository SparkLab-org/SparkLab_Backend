package com.sparkLab.study.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyCommentRes {

    private Long dailyPlanId;
    private String comment;
}
