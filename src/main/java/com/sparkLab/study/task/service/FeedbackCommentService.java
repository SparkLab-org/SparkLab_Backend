package com.sparkLab.study.task.service;

import com.sparkLab.study.task.dto.feedback.FeedbackCommentCreateRequest;
import com.sparkLab.study.task.dto.feedback.FeedbackCommentResponse;
import com.sparkLab.study.task.entity.Feedback;
import com.sparkLab.study.task.entity.FeedbackComment;
import com.sparkLab.study.task.exception.TaskResourceNotFoundException;
import com.sparkLab.study.task.repository.FeedbackCommentRepository;
import com.sparkLab.study.task.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackCommentService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackCommentRepository feedbackCommentRepository;

    @Transactional
    public FeedbackCommentResponse create(Long feedbackId, FeedbackCommentCreateRequest request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new TaskResourceNotFoundException("피드백을 찾을 수 없습니다. feedbackId=" + feedbackId));

        FeedbackComment comment = FeedbackComment.of(feedback, request.getType(), request.getContent());
        return toResponse(feedbackCommentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<FeedbackCommentResponse> list(Long feedbackId) {
        List<FeedbackComment> comments = feedbackCommentRepository
                .findByFeedback_FeedbackIdOrderByCreateTimeAsc(feedbackId);
        return comments.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private FeedbackCommentResponse toResponse(FeedbackComment comment) {
        return FeedbackCommentResponse.builder()
                .feedbackCommentId(comment.getFeedbackCommentId())
                .feedbackId(comment.getFeedback().getFeedbackId())
                .type(comment.getType())
                .content(comment.getContent())
                .createTime(comment.getFeedback().getCreateTime())
                .updateTime(comment.getFeedback().getUpdateTime())
                .build();
    }
}
