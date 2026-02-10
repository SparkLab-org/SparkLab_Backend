package com.sparkLab.study.task.repository;

import com.sparkLab.study.task.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByTodoItem_Mentee_MenteeIdAndTodoItem_TargetDateBeforeOrderByTodoItem_TargetDateAscAssignmentIdAsc(
            Long menteeId,
            LocalDate targetDate
    );

    List<Assignment> findByTodoItem_TargetDate(LocalDate targetDate);

    /** 멘티별 과제 목록 (과제일 기준 내림차순) */
    List<Assignment> findByTodoItem_Mentee_MenteeIdOrderByTodoItem_TargetDateDescCreateTimeDesc(Long menteeId);
}
