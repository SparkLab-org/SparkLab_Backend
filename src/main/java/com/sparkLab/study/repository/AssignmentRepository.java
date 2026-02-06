package com.sparkLab.study.repository;

import com.sparkLab.study.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByTodoItem_Mentee_MenteeIdAndTodoItem_TargetDateBeforeOrderByTodoItem_TargetDateAscAssignmentIdAsc(
            Long menteeId,
            LocalDate targetDate
    );
}
