package com.sparkLab.study.planner.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DailyCommentDto {

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Req {
        private Long menteeId;
        private Long dailyPlanId;
        private String comment;
    }

    @Getter
    @Builder
    public static class Res {
        private Long dailyPlanId;
        private String comment;
    }
}
