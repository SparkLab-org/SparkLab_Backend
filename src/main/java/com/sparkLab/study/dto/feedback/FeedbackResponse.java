package com.sparkLab.study.dto.feedback;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FeedbackResponse {
    private Long feedbackId;
    private Long mentorId;
    private Long menteeId;
    private Long todoItemId;
    private LocalDateTime targetDate;
    private Boolean isImportant;
    private String summary;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
