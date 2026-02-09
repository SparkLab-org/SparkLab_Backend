package com.sparkLab.study.planner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DailyCommentReq {

    private Long dailyPlanId;
    private String comment;
}
