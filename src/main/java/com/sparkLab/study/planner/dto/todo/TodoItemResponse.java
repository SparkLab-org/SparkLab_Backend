package com.sparkLab.study.planner.dto.todo;

import com.sparkLab.study.common.constant.Subject;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItemResponse {

    private Long todoItemId;
    private Long plannerId;
    private Long assignmentId;
    /** 과제 자료 PDF URL (type=ASSIGNMENT일 때) */
    private String materialFileUrl;
    private LocalDate targetDate;
    private String title;
    private Subject subject;
    private String type;
    private Boolean isFixed;
    private String status;
    private Integer plannedMinutes;
    private Integer actualMinutes;
    private Integer actualSeconds;
    private LocalDateTime completedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
