package com.sparkLab.study.task.repository;

import com.sparkLab.study.task.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByMentee_MenteeIdOrderByCreateTimeAsc(Long menteeId);
    List<Feedback> findByMentee_MenteeIdAndIsImportantTrueOrderByCreateTimeDesc(Long menteeId);
    List<Feedback> findByMentor_MentorIdOrderByCreateTimeAsc(Long mentorId);
    List<Feedback> findByTodoItem_TodoItemIdOrderByCreateTimeAsc(Long todoItemId);
    List<Feedback> findAllByOrderByCreateTimeAsc();
    boolean existsByTodoItem_TodoItemId(Long todoItemId);
}
