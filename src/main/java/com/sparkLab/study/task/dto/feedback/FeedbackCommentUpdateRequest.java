package com.sparkLab.study.task.dto.feedback;

import com.sparkLab.study.task.constant.FeedbackCommentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackCommentUpdateRequest {
    private FeedbackCommentType type;
    private String content;
}
