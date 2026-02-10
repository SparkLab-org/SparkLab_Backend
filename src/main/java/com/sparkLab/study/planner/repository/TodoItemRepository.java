package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.TodoItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findByDailyPlan_dailyPlanIdOrderByCreateTimeAsc(Long plannerId);

    List<TodoItem> findByDailyPlan_PlanDateOrderByCreateTimeAsc(LocalDate planDate);

    List<TodoItem> findByDailyPlan_Mentee_MenteeIdAndDailyPlan_PlanDateOrderByCreateTimeAsc(Long menteeId, LocalDate planDate);

    /** 멘티별 할일 (날짜 필터 없이 전체) */
    List<TodoItem> findByMentee_MenteeIdOrderByTargetDateDescCreateTimeAsc(Long menteeId);

    boolean existsByTodoItemIdAndMentee_MenteeId(Long todoItemId, Long menteeId);
}
