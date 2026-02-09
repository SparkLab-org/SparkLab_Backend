package com.sparkLab.study.dto.todo;

import com.sparkLab.study.constant.Subject;
import com.sparkLab.study.dto.assignment.AssignmentSubmissionResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** 과제(할일) 상세 조회 응답: Todo 내용 + 첨부 파일 + 멘티 제출 목록 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoAssignmentDetailResponse {

    private Long todoItemId;
    private Long plannerId;
    private Long assignmentId;
    private LocalDate targetDate;
    private String title;
    private Subject subject;
    private String type;
    private String goal;
    private String materialType;
    private String materialUrl;
    private Boolean isFixed;
    private String status;
    private Integer plannedMinutes;
    private Integer actualMinutes;
    private Integer actualSeconds;
    private LocalDateTime completedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<AssignmentSubmissionResponse> submissions;
}
