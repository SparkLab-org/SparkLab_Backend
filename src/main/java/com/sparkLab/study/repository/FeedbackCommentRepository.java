package com.sparkLab.study.repository;

import com.sparkLab.study.entity.FeedbackComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackCommentRepository extends JpaRepository<FeedbackComment, Long> {
    List<FeedbackComment> findByFeedback_FeedbackIdOrderByCreateTimeAsc(Long feedbackId);
}
