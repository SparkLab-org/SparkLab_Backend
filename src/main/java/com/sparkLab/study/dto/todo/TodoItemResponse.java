package com.sparkLab.study.dto.todo;

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
    private LocalDate targetDate;
    private String title;
    private String subject;
    private String type;
    private Boolean isFixed;
    private String status;
    private Integer plannedMinutes;
    private Integer actualMinutes;
    private LocalDateTime completedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
