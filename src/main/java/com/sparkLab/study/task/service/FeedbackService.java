package com.sparkLab.study.task.service;

import com.sparkLab.study.task.dto.FeedbackCreateRequest;
import com.sparkLab.study.task.dto.FeedbackResponse;
import com.sparkLab.study.task.dto.FeedbackUpdateRequest;
import com.sparkLab.study.activity.service.NotificationService;
import com.sparkLab.study.task.entity.Feedback;
import com.sparkLab.study.account.entity.Mentee;
import com.sparkLab.study.account.entity.Mentor;
import com.sparkLab.study.planner.entity.TodoItem;
import com.sparkLab.study.common.exception.PlannerResourceNotFoundException;
import com.sparkLab.study.common.exception.TaskResourceNotFoundException;
import com.sparkLab.study.account.repository.MenteeRepository;
import com.sparkLab.study.account.repository.MentorRepository;
import com.sparkLab.study.planner.repository.TodoItemRepository;
import com.sparkLab.study.task.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final TodoItemRepository todoItemRepository;
    private final NotificationService notificationService;

    @Transactional
    public FeedbackResponse create(FeedbackCreateRequest request) {
        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘토를 찾을 수 없습니다. mentorId=" + request.getMentorId()));
        Mentee mentee = menteeRepository.findById(request.getMenteeId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + request.getMenteeId()));
        TodoItem todoItem = null;
        if (request.getTodoItemId() != null) {
            todoItem = todoItemRepository.findById(request.getTodoItemId())
                    .orElseThrow(() -> new PlannerResourceNotFoundException("할일을 찾을 수 없습니다. todoItemId=" + request.getTodoItemId()));
        }
        Feedback feedback = Feedback.builder()
                .mentor(mentor)
                .mentee(mentee)
                .todoItem(todoItem)
                .targetDate(request.getTargetDate())
                .isImportant(request.getIsImportant())
                .summary(resolveSummary(request.getIsImportant(), request.getContent(), request.getSummary()))
                .content(request.getContent())
                .build();
        Feedback saved = feedbackRepository.save(feedback);
        notificationService.notifyFeedback(saved);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> list(Long menteeId, Long mentorId, Long todoItemId) {
        List<Feedback> feedbacks;
        if (todoItemId != null) {
            feedbacks = feedbackRepository.findByTodoItem_TodoItemIdOrderByCreateTimeAsc(todoItemId);
        } else if (menteeId != null) {
            feedbacks = feedbackRepository.findByMentee_MenteeIdOrderByCreateTimeAsc(menteeId);
        } else if (mentorId != null) {
            feedbacks = feedbackRepository.findByMentor_MentorIdOrderByCreateTimeAsc(mentorId);
        } else {
            feedbacks = feedbackRepository.findAllByOrderByCreateTimeAsc();
        }
        return feedbacks.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getOne(Long feedbackId) {
        return toResponse(findFeedback(feedbackId));
    }

    @Transactional
    public FeedbackResponse update(Long feedbackId, FeedbackUpdateRequest request) {
        Feedback feedback = findFeedback(feedbackId);
        if (request.getTodoItemId() != null) {
            TodoItem todoItem = todoItemRepository.findById(request.getTodoItemId())
                    .orElseThrow(() -> new PlannerResourceNotFoundException("할일을 찾을 수 없습니다. todoItemId=" + request.getTodoItemId()));
            feedback.setTodoItem(todoItem);
        }
        if (request.getTargetDate() != null) feedback.setTargetDate(request.getTargetDate());
        if (request.getIsImportant() != null) feedback.setIsImportant(request.getIsImportant());
        if (request.getContent() != null) feedback.setContent(request.getContent());
        feedback.setSummary(resolveSummary(
                feedback.getIsImportant(),
                feedback.getContent(),
                request.getSummary() != null ? request.getSummary() : feedback.getSummary()
        ));
        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public void delete(Long feedbackId) {
        Feedback feedback = findFeedback(feedbackId);
        feedbackRepository.delete(feedback);
    }

    private Feedback findFeedback(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new TaskResourceNotFoundException("피드백을 찾을 수 없습니다. feedbackId=" + feedbackId));
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        Long todoItemId = feedback.getTodoItem() == null ? null : feedback.getTodoItem().getTodoItemId();
        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .mentorId(feedback.getMentor().getMentorId())
                .menteeId(feedback.getMentee().getMenteeId())
                .todoItemId(todoItemId)
                .targetDate(feedback.getTargetDate())
                .isImportant(feedback.getIsImportant())
                .summary(feedback.getSummary())
                .content(feedback.getContent())
                .createTime(feedback.getCreateTime())
                .updateTime(feedback.getUpdateTime())
                .build();
    }

    private String resolveSummary(Boolean isImportant, String content, String fallbackSummary) {
        if (Boolean.TRUE.equals(isImportant)) {
            return "중요";
        }
        String autoSummary = extractSummaryFromContent(content);
        if (autoSummary == null || autoSummary.isBlank()) {
            return fallbackSummary;
        }
        return autoSummary;
    }

    private String extractSummaryFromContent(String content) {
        if (content == null) {
            return null;
        }
        String normalized = content.trim();
        if (normalized.isEmpty()) {
            return "";
        }
        String[] sentences = normalized.split("(?<=[.!?])\\s+");
        if (sentences.length == 1) {
            return sentences[0];
        }
        return sentences[0] + " " + sentences[1];
    }
}
