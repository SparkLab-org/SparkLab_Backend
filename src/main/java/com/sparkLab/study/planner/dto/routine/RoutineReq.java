package com.sparkLab.study.planner.dto.routine;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoutineReq {
    private String title;
    private String description;
    private Integer targetMinutes;
}

