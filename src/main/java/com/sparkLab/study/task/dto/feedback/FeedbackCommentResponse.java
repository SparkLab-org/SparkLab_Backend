package com.sparkLab.study.task.dto.feedback;

import com.sparkLab.study.task.constant.FeedbackCommentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FeedbackCommentResponse {

    private Long feedbackCommentId;
    private Long feedbackId;
    private FeedbackCommentType type;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
