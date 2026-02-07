package com.sparkLab.study.task.dto.feedback;

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
