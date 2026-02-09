package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.TodoItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findByDailyPlan_dailyPlanIdOrderByCreateTimeAsc(Long plannerId);

    List<TodoItem> findByDailyPlan_PlanDateOrderByCreateTimeAsc(LocalDate planDate);

    boolean existsByTodoItemIdAndMentee_MenteeId(Long todoItemId, Long menteeId);

    Optional<TodoItem> findByDailyPlan_DailyPlanIdOrderByCreateTimeAsc(Long plannerId);
}
