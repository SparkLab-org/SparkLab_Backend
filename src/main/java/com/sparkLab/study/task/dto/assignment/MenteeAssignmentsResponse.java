package com.sparkLab.study.task.dto.assignment;

import com.sparkLab.study.user.constant.ActiveLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 멘티별 과제 목록 (멘토가 전체 멘티 과제를 조회할 때 사용)
 */
@Getter
@Builder
public class MenteeAssignmentsResponse {
    private Long menteeId;
    private String accountId;
    private ActiveLevel activeLevel;
    private List<AssignmentResponse> assignments;
}
