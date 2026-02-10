package com.sparkLab.study.task.repository;

import com.sparkLab.study.task.entity.FeedbackBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackBookmarkRepository extends JpaRepository<FeedbackBookmark, Long> {
    List<FeedbackBookmark> findByMentee_MenteeIdOrderByCreateTimeDesc(Long menteeId);
    Optional<FeedbackBookmark> findByMentee_MenteeIdAndFeedback_FeedbackId(Long menteeId, Long feedbackId);
    boolean existsByMentee_MenteeIdAndFeedback_FeedbackId(Long menteeId, Long feedbackId);
    void deleteByMentee_MenteeIdAndFeedback_FeedbackId(Long menteeId, Long feedbackId);
}
