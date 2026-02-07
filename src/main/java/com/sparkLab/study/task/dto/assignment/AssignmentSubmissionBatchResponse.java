package com.sparkLab.study.task.dto.assignment;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AssignmentSubmissionBatchResponse {
    private List<AssignmentSubmissionResponse> submissions;
}
