package com.sparkLab.study.dto.feedback;

import com.sparkLab.study.constant.Subject;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackUpdateRequest {

    private Long todoItemId;

    private Long assignmentId;

    private LocalDateTime targetDate;

    private Subject subject;

    private String content;
}
