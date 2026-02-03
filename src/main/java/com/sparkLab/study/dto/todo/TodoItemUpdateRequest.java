package com.sparkLab.study.dto.todo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItemUpdateRequest {

    private String title;
    private String subject;
    private String type;
    private String status;
    private Integer plannedMinutes;
    private Integer actualMinutes;
    private LocalDateTime completedAt;
}
