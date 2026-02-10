package com.sparkLab.study.planner.dto.todo;

import com.sparkLab.study.user.constant.ActiveLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 멘티별 할일 목록 - 멘티당 날짜별로 그룹화
 */
@Getter
@Builder
public class MenteeTodosResponse {
    private Long menteeId;
    private String accountId;
    private ActiveLevel activeLevel;
    /** 날짜별 할일 그룹 (날짜 내림차순) */
    private List<DateTodosGroup> todosByDate;
}
