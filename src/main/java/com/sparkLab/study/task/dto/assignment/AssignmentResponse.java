package com.sparkLab.study.task.dto.assignment;

import com.sparkLab.study.common.constant.Subject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AssignmentResponse {
    private Long assignmentId;
    private Long todoItemId;
    private Long menteeId;
    private String materialTitle;
    private Subject subject;
    private LocalDate targetDate;
    private LocalDateTime createTime;
    /** 해당 멘티가 이 과제를 제출했는지 여부 */
    private boolean submitted;
    /** 최신 제출 ID (있을 경우) */
    private Long latestSubmissionId;
}
