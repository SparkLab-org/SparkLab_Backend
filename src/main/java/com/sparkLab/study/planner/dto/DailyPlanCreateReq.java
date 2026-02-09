package com.sparkLab.study.planner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class DailyPlanCreateReq {

    private LocalDate planDate;
    private String comment;
}
