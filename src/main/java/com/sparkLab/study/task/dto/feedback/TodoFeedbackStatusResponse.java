package com.sparkLab.study.task.dto.feedback;

import com.sparkLab.study.common.constant.Subject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TodoFeedbackStatusResponse {
    private Long todoItemId;
    private String title;
    private Subject subject;
    private String type;
    private LocalDate targetDate;
    private boolean hasFeedback;
}
