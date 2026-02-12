package com.sparkLab.study.planner.dto.todo;

import com.sparkLab.study.common.constant.Subject;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItemResponse {

    private Long todoItemId;
    private Long plannerId;
    private Long assignmentId;
    /** 멘토가 첨부한 자료 파일 URL (다운로드: {API_BASE}{materialFileUrl}) */
    private String materialFileUrl;
    /** 첨부 자료 타입 (예: PDF) */
    private String materialType;
    private LocalDate targetDate;
    private String title;
    private Subject subject;
    private String type;
    private Boolean isFixed;
    private String status;
    private Integer plannedMinutes;
    private Integer actualMinutes;
    private Integer actualSeconds;
    private LocalDateTime completedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
