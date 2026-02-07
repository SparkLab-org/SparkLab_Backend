package com.sparkLab.study.task.repository;

import com.sparkLab.study.task.entity.FeedbackComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackCommentRepository extends JpaRepository<FeedbackComment, Long> {
    List<FeedbackComment> findByFeedback_FeedbackIdOrderByCreateTimeAsc(Long feedbackId);
}
