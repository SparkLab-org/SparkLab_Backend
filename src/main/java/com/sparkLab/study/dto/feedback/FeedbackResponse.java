package com.sparkLab.study.dto.feedback;

import com.sparkLab.study.constant.Subject;
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
    private Subject subject;
    private String summary;
    private String importantComment;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
