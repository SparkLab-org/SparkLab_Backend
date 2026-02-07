package com.sparkLab.study.planner.dto;

import com.sparkLab.study.common.constant.Subject;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItemUpdateRequest {

    private String title;
    private Subject subject;
    private String type;
    private String status;
    private Integer plannedMinutes;
    private Integer actualMinutes;
    private LocalDateTime completedAt;
}
