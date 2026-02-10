package com.sparkLab.study.planner.dto.todo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/** 날짜별 할일 그룹 */
@Getter
@Builder
public class DateTodosGroup {
    private LocalDate planDate;
    private List<TodoItemResponse> todos;
}
