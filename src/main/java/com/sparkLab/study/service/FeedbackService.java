package com.sparkLab.study.service;

import com.sparkLab.study.dto.feedback.FeedbackCreateRequest;
import com.sparkLab.study.dto.feedback.FeedbackResponse;
import com.sparkLab.study.dto.feedback.FeedbackUpdateRequest;
import com.sparkLab.study.entity.Assignment;
import com.sparkLab.study.entity.Feedback;
import com.sparkLab.study.entity.Mentee;
import com.sparkLab.study.entity.Mentor;
import com.sparkLab.study.entity.TodoItem;
import com.sparkLab.study.exception.PlannerResourceNotFoundException;
import com.sparkLab.study.exception.TaskResourceNotFoundException;
import com.sparkLab.study.repository.AssignmentRepository;
import com.sparkLab.study.repository.FeedbackRepository;
import com.sparkLab.study.repository.MenteeRepository;
import com.sparkLab.study.repository.MentorRepository;
import com.sparkLab.study.repository.TodoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparkLab.study.constant.Subject;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final TodoItemRepository todoItemRepository;
    private final AssignmentRepository assignmentRepository;
    private final NotificationService notificationService;

    @Transactional
    public FeedbackResponse create(FeedbackCreateRequest request) {
        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘토를 찾을 수 없습니다. mentorId=" + request.getMentorId()));
        Mentee mentee = menteeRepository.findById(request.getMenteeId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + request.getMenteeId()));
        TodoItem todoItem = null;
        Assignment assignment = null;
        if (request.getTodoItemId() != null && request.getAssignmentId() != null) {
            throw new IllegalArgumentException("todoItemId와 assignmentId는 동시에 지정할 수 없습니다.");
        }
        if (request.getTodoItemId() != null) {
            todoItem = todoItemRepository.findById(request.getTodoItemId())
                    .orElseThrow(() -> new PlannerResourceNotFoundException("할일을 찾을 수 없습니다. todoItemId=" + request.getTodoItemId()));
        }
        if (request.getAssignmentId() != null) {
            assignment = assignmentRepository.findById(request.getAssignmentId())
                    .orElseThrow(() -> new PlannerResourceNotFoundException("과제를 찾을 수 없습니다. assignmentId=" + request.getAssignmentId()));
        }
        Feedback feedback = buildFeedback(request, mentor, mentee, todoItem, assignment);
        Feedback saved = feedbackRepository.save(feedback);
        notificationService.notifyFeedback(saved);
        return toResponse(saved);
    }

    @Transactional
    public FeedbackResponse createForTodo(Long todoItemId, FeedbackCreateRequest request) {
        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘토를 찾을 수 없습니다. mentorId=" + request.getMentorId()));
        Mentee mentee = menteeRepository.findById(request.getMenteeId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + request.getMenteeId()));
        TodoItem todoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new PlannerResourceNotFoundException("할일을 찾을 수 없습니다. todoItemId=" + todoItemId));
        Feedback feedback = buildFeedback(request, mentor, mentee, todoItem, null);
        Feedback saved = feedbackRepository.save(feedback);
        notificationService.notifyFeedback(saved);
        return toResponse(saved);
    }

    @Transactional
    public FeedbackResponse createForAssignment(Long assignmentId, FeedbackCreateRequest request) {
        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘토를 찾을 수 없습니다. mentorId=" + request.getMentorId()));
        Mentee mentee = menteeRepository.findById(request.getMenteeId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + request.getMenteeId()));
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new PlannerResourceNotFoundException("과제를 찾을 수 없습니다. assignmentId=" + assignmentId));
        Feedback feedback = buildFeedback(request, mentor, mentee, null, assignment);
        Feedback saved = feedbackRepository.save(feedback);
        notificationService.notifyFeedback(saved);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> list(Long menteeId, Long mentorId, Long todoItemId, Subject subject) {
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
        return feedbacks.stream()
                .filter(feedback -> subject == null || subject == feedback.getSubject())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> listByAssignment(Long assignmentId, Subject subject) {
        return feedbackRepository.findByAssignment_AssignmentIdOrderByCreateTimeAsc(assignmentId).stream()
                .filter(feedback -> subject == null || subject == feedback.getSubject())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> listByTodo(Long todoItemId, Subject subject) {
        return feedbackRepository.findByTodoItem_TodoItemIdOrderByCreateTimeAsc(todoItemId).stream()
                .filter(feedback -> subject == null || subject == feedback.getSubject())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getForTodo(Long todoItemId, Long feedbackId) {
        return toResponse(findTodoFeedback(todoItemId, feedbackId));
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getForAssignment(Long assignmentId, Long feedbackId) {
        return toResponse(findAssignmentFeedback(assignmentId, feedbackId));
    }

    @Transactional
    public FeedbackResponse updateForTodo(Long todoItemId, Long feedbackId, FeedbackUpdateRequest request) {
        Feedback feedback = findTodoFeedback(todoItemId, feedbackId);
        applyUpdate(feedback, request);
        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public FeedbackResponse updateForAssignment(Long assignmentId, Long feedbackId, FeedbackUpdateRequest request) {
        Feedback feedback = findAssignmentFeedback(assignmentId, feedbackId);
        applyUpdate(feedback, request);
        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public void deleteForTodo(Long todoItemId, Long feedbackId) {
        Feedback feedback = findTodoFeedback(todoItemId, feedbackId);
        feedbackRepository.delete(feedback);
    }

    @Transactional
    public void deleteForAssignment(Long assignmentId, Long feedbackId) {
        Feedback feedback = findAssignmentFeedback(assignmentId, feedbackId);
        feedbackRepository.delete(feedback);
    }

    private Feedback findFeedback(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new TaskResourceNotFoundException("피드백을 찾을 수 없습니다. feedbackId=" + feedbackId));
    }

    private Feedback findTodoFeedback(Long todoItemId, Long feedbackId) {
        Feedback feedback = findFeedback(feedbackId);
        if (feedback.getTodoItem() == null || !feedback.getTodoItem().getTodoItemId().equals(todoItemId)) {
            throw new TaskResourceNotFoundException("할일 피드백을 찾을 수 없습니다. todoItemId=" + todoItemId);
        }
        return feedback;
    }

    private Feedback findAssignmentFeedback(Long assignmentId, Long feedbackId) {
        Feedback feedback = findFeedback(feedbackId);
        if (feedback.getAssignment() == null || !feedback.getAssignment().getAssignmentId().equals(assignmentId)) {
            throw new TaskResourceNotFoundException("과제 피드백을 찾을 수 없습니다. assignmentId=" + assignmentId);
        }
        return feedback;
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        Long todoItemId = feedback.getTodoItem() == null ? null : feedback.getTodoItem().getTodoItemId();
        Long assignmentId = feedback.getAssignment() == null ? null : feedback.getAssignment().getAssignmentId();
        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .mentorId(feedback.getMentor().getMentorId())
                .menteeId(feedback.getMentee().getMenteeId())
                .todoItemId(todoItemId)
                .assignmentId(assignmentId)
                .targetDate(feedback.getTargetDate())
                .subject(feedback.getSubject())
                .summary(feedback.getSummary())
                .importantComment(feedback.getImportantComment())
                .content(feedback.getContent())
                .createTime(feedback.getCreateTime())
                .updateTime(feedback.getUpdateTime())
                .build();
    }

    @Transactional
    public FeedbackResponse updateImportantForTodo(Long todoItemId, Long feedbackId, String importantComment) {
        Feedback feedback = findTodoFeedback(todoItemId, feedbackId);
        String normalized = normalizeImportantComment(importantComment);
        feedback.setImportantComment(normalized);
        feedback.setSummary(resolveSummary(feedback.getContent(), feedback.getImportantComment()));
        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public FeedbackResponse deleteImportantForTodo(Long todoItemId, Long feedbackId) {
        Feedback feedback = findTodoFeedback(todoItemId, feedbackId);
        feedback.setImportantComment(null);
        feedback.setSummary(resolveSummary(feedback.getContent(), null));
        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public FeedbackResponse updateImportantForAssignment(Long assignmentId, Long feedbackId, String importantComment) {
        Feedback feedback = findAssignmentFeedback(assignmentId, feedbackId);
        String normalized = normalizeImportantComment(importantComment);
        feedback.setImportantComment(normalized);
        feedback.setSummary(resolveSummary(feedback.getContent(), feedback.getImportantComment()));
        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public FeedbackResponse deleteImportantForAssignment(Long assignmentId, Long feedbackId) {
        Feedback feedback = findAssignmentFeedback(assignmentId, feedbackId);
        feedback.setImportantComment(null);
        feedback.setSummary(resolveSummary(feedback.getContent(), null));
        return toResponse(feedbackRepository.save(feedback));
    }

    private String resolveSummary(String content, String importantComment) {
        if (!isBlank(importantComment)) {
            return importantComment.trim();
        }
        return extractSummaryFromContent(content);
    }

    private void applyUpdate(Feedback feedback, FeedbackUpdateRequest request) {
        if (request.getTargetDate() != null) feedback.setTargetDate(request.getTargetDate());
        if (request.getSubject() != null) feedback.setSubject(request.getSubject());
        if (request.getContent() != null) feedback.setContent(request.getContent());
        feedback.setSummary(resolveSummary(
                feedback.getContent(),
                feedback.getImportantComment()
        ));
    }

    private Feedback buildFeedback(
            FeedbackCreateRequest request,
            Mentor mentor,
            Mentee mentee,
            TodoItem todoItem,
            Assignment assignment
    ) {
        return Feedback.builder()
                .mentor(mentor)
                .mentee(mentee)
                .todoItem(todoItem)
                .assignment(assignment)
                .targetDate(request.getTargetDate())
                .subject(request.getSubject())
                .summary(resolveSummary(request.getContent(), null))
                .content(request.getContent())
                .build();
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
        return sentences[0];
    }

    private String normalizeImportantComment(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
