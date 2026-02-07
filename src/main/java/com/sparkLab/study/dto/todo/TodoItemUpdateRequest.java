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
public class TodoItemUpdateRequest {

    private String title;
    private LocalDate targetDate;
    private Subject subject;
    private String type;
    private String status;
    private Integer plannedMinutes;
    private Integer actualMinutes;
    private Integer actualSeconds;
    private LocalDateTime completedAt;
}
