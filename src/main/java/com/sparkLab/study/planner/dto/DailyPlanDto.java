package com.sparkLab.study.planner.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class DailyPlanDto {

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Req {
        private Long menteeId;
        private LocalDate planDate;
        private String comment;

    }

    @Getter
    @Builder
    public static class Res {
        private Long dailyPlanId;
        private boolean created;
    }
}
