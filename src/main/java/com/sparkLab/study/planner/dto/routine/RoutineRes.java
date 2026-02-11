package com.sparkLab.study.planner.dto.routine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoutineRes {
    private Long routineId;
    private String title;
    private String description;
    private Integer targetMinutes;
    private Boolean isActive;
}
