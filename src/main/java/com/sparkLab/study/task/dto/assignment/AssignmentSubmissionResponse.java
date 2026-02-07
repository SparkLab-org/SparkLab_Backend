package com.sparkLab.study.task.dto.assignment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AssignmentSubmissionResponse {
    private Long submissionId;
    private Long assignmentId;
    private Long menteeId;
    private String imageUrl;
    private String comment;
    private String status;
    private LocalDateTime createTime;
}
