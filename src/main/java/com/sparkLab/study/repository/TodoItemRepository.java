package com.sparkLab.study.repository;

import com.sparkLab.study.entity.TodoItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findByPlanner_PlannerIdOrderByCreateTimeAsc(Long plannerId);

    List<TodoItem> findByMentee_MenteeIdAndPlanner_PlanDateOrderByCreateTimeAsc(Long menteeId, LocalDate planDate);

    boolean existsByTodoItemIdAndMentee_MenteeId(Long todoItemId, Long menteeId);
}
