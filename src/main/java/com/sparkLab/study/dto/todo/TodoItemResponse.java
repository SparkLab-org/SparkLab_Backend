package com.sparkLab.study.dto.todo;

import com.sparkLab.study.constant.Subject;
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
    private Subject subject;
    private String type;
    private String goal;
    private String materialType;
    private String materialUrl;
    private Boolean isFixed;
    private String status;
    private Integer plannedMinutes;
    private Integer actualMinutes;
    private Integer actualSeconds;
    private LocalDateTime completedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
