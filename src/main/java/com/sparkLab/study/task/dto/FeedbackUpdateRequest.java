package com.sparkLab.study.task.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackUpdateRequest {

    private Long todoItemId;

    private LocalDateTime targetDate;

    private Boolean isImportant;

    private String summary;

    private String content;
}
