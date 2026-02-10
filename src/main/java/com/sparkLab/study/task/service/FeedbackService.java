package com.sparkLab.study.task.service;

import com.sparkLab.study.common.constant.Subject;
import com.sparkLab.study.common.exception.NotOwnerException;
import com.sparkLab.study.planner.exception.PlannerResourceNotFoundException;
import com.sparkLab.study.task.dto.feedback.FeedbackCreateRequest;
import com.sparkLab.study.task.dto.feedback.FeedbackResponse;
import com.sparkLab.study.task.dto.feedback.FeedbackUpdateRequest;
import com.sparkLab.study.task.dto.feedback.TodoFeedbackStatusResponse;
import com.sparkLab.study.activity.service.NotificationService;
import com.sparkLab.study.task.entity.Feedback;
import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.entity.Mentor;
import com.sparkLab.study.planner.entity.TodoItem;
import com.sparkLab.study.task.exception.TaskResourceNotFoundException;
import com.sparkLab.study.user.repository.MenteeRepository;
import com.sparkLab.study.user.repository.MentorRepository;
import com.sparkLab.study.planner.repository.TodoItemRepository;
import com.sparkLab.study.task.repository.FeedbackBookmarkRepository;
import com.sparkLab.study.task.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackBookmarkRepository feedbackBookmarkRepository;
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
                .title(request.getTitle())
                .targetDate(request.getTargetDate())
                .isImportant(request.getIsImportant())
                .summary(resolveSummary(request.getIsImportant(), request.getContent()))
                .content(request.getContent())
                .build();
        Feedback saved = feedbackRepository.save(feedback);
        notificationService.notifyFeedback(saved);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> list(Long menteeId, Long mentorId, Long todoItemId,
                                      Boolean isImportant, LocalDateTime targetDate, Subject subject, String sort) {
        List<Feedback> feedbacks;
        if (todoItemId != null) {
            feedbacks = feedbackRepository.findByTodoItem_TodoItemIdOrderByCreateTimeAsc(todoItemId);
        } else if (menteeId != null && "bookmarked".equals(sort)) {
            List<com.sparkLab.study.task.entity.FeedbackBookmark> bookmarks =
                    feedbackBookmarkRepository.findByMentee_MenteeIdOrderByCreateTimeDesc(menteeId);
            feedbacks = bookmarks.stream()
                    .map(com.sparkLab.study.task.entity.FeedbackBookmark::getFeedback)
                    .collect(Collectors.toList());
        } else if (menteeId != null) {
            feedbacks = isImportant != null && Boolean.TRUE.equals(isImportant)
                    ? feedbackRepository.findByMentee_MenteeIdAndIsImportantTrueOrderByCreateTimeDesc(menteeId)
                    : feedbackRepository.findByMentee_MenteeIdOrderByCreateTimeAsc(menteeId);
        } else if (mentorId != null) {
            feedbacks = feedbackRepository.findByMentor_MentorIdOrderByCreateTimeAsc(mentorId);
        } else {
            feedbacks = feedbackRepository.findAllByOrderByCreateTimeAsc();
        }
        Stream<Feedback> stream = feedbacks.stream();
        if (targetDate != null) {
            stream = stream.filter(f -> f.getTargetDate() != null && f.getTargetDate().toLocalDate().equals(targetDate.toLocalDate()));
        }
        if (subject != null && (menteeId != null || mentorId != null)) {
            stream = stream.filter(f -> f.getTodoItem() != null && subject.equals(f.getTodoItem().getSubject()));
        }
        if ("latest".equals(sort) && (menteeId == null || !"bookmarked".equals(sort))) {
            stream = stream.sorted(Comparator.comparing(Feedback::getCreateTime).reversed());
        }
        final boolean bookmarkedSort = menteeId != null && "bookmarked".equals(sort);
        Set<Long> bookmarkedIds = (!bookmarkedSort && menteeId != null)
                ? feedbackBookmarkRepository.findByMentee_MenteeIdOrderByCreateTimeDesc(menteeId).stream()
                        .map(b -> b.getFeedback().getFeedbackId())
                        .collect(Collectors.toSet())
                : Set.of();
        Set<Long> finalBookmarkedIds = bookmarkedIds;
        return stream.map(f -> toResponse(f, bookmarkedSort ? Boolean.TRUE : finalBookmarkedIds.contains(f.getFeedbackId()))).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getOne(Long feedbackId, Long currentMenteeIdForCheck) {
        Feedback feedback = findFeedback(feedbackId);
        if (currentMenteeIdForCheck != null && (feedback.getMentee() == null || !currentMenteeIdForCheck.equals(feedback.getMentee().getMenteeId()))) {
            throw new NotOwnerException("Feedback", feedbackId);
        }
        Boolean isBookmarked = currentMenteeIdForCheck != null && feedbackBookmarkRepository.existsByMentee_MenteeIdAndFeedback_FeedbackId(currentMenteeIdForCheck, feedbackId);
        return toResponse(feedback, isBookmarked);
    }

    @Transactional
    public FeedbackResponse update(Long feedbackId, FeedbackUpdateRequest request) {
        Feedback feedback = findFeedback(feedbackId);
        if (request.getTodoItemId() != null) {
            TodoItem todoItem = todoItemRepository.findById(request.getTodoItemId())
                    .orElseThrow(() -> new PlannerResourceNotFoundException("할일을 찾을 수 없습니다. todoItemId=" + request.getTodoItemId()));
            feedback.setTodoItem(todoItem);
        }
        if (request.getTitle() != null) feedback.setTitle(request.getTitle());
        if (request.getTargetDate() != null) feedback.setTargetDate(request.getTargetDate());
        if (request.getIsImportant() != null) feedback.setIsImportant(request.getIsImportant());
        if (request.getContent() != null) feedback.setContent(request.getContent());
        feedback.setSummary(resolveSummary(feedback.getIsImportant(), feedback.getContent()));
        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public void delete(Long feedbackId) {
        Feedback feedback = findFeedback(feedbackId);
        feedbackRepository.delete(feedback);
    }

    @Transactional
    public FeedbackResponse updateImportant(Long feedbackId, boolean isImportant) {
        Feedback feedback = findFeedback(feedbackId);
        feedback.setIsImportant(isImportant);
        feedback.setSummary(resolveSummary(isImportant, feedback.getContent()));
        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public void addBookmark(Long menteeId, Long feedbackId) {
        Feedback feedback = findFeedback(feedbackId);
        if (feedback.getMentee() == null || !feedback.getMentee().getMenteeId().equals(menteeId)) {
            throw new NotOwnerException("Feedback", feedbackId);
        }
        if (feedbackBookmarkRepository.existsByMentee_MenteeIdAndFeedback_FeedbackId(menteeId, feedbackId)) {
            return;
        }
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + menteeId));
        feedbackBookmarkRepository.save(com.sparkLab.study.task.entity.FeedbackBookmark.builder()
                .mentee(mentee)
                .feedback(feedback)
                .build());
    }

    @Transactional
    public void removeBookmark(Long menteeId, Long feedbackId) {
        feedbackBookmarkRepository.deleteByMentee_MenteeIdAndFeedback_FeedbackId(menteeId, feedbackId);
    }

    @Transactional(readOnly = true)
    public List<TodoFeedbackStatusResponse> getTodoStatus(Long menteeId, LocalDate planDate) {
        List<TodoItem> todos = todoItemRepository.findByDailyPlan_Mentee_MenteeIdAndDailyPlan_PlanDateOrderByCreateTimeAsc(menteeId, planDate);
        return todos.stream()
                .map(todo -> TodoFeedbackStatusResponse.builder()
                        .todoItemId(todo.getTodoItemId())
                        .title(todo.getTitle())
                        .subject(todo.getSubject())
                        .type(todo.getType())
                        .targetDate(todo.getTargetDate())
                        .hasFeedback(feedbackRepository.existsByTodoItem_TodoItemId(todo.getTodoItemId()))
                        .build())
                .collect(Collectors.toList());
    }

    private Feedback findFeedback(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new TaskResourceNotFoundException("피드백을 찾을 수 없습니다. feedbackId=" + feedbackId));
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        return toResponse(feedback, null);
    }

    private FeedbackResponse toResponse(Feedback feedback, Boolean isBookmarked) {
        TodoItem todo = feedback.getTodoItem();
        Long todoItemId = todo == null ? null : todo.getTodoItemId();
        String todoTitle = todo == null ? null : todo.getTitle();
        Subject subject = todo == null ? null : todo.getSubject();
        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .mentorId(feedback.getMentor().getMentorId())
                .menteeId(feedback.getMentee().getMenteeId())
                .todoItemId(todoItemId)
                .todoTitle(todoTitle)
                .subject(subject)
                .title(feedback.getTitle())
                .targetDate(feedback.getTargetDate())
                .isImportant(feedback.getIsImportant())
                .summary(feedback.getSummary())
                .content(feedback.getContent())
                .isBookmarked(isBookmarked != null && isBookmarked)
                .createTime(feedback.getCreateTime())
                .updateTime(feedback.getUpdateTime())
                .build();
    }

    private String resolveSummary(Boolean isImportant, String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        if (Boolean.TRUE.equals(isImportant)) {
            return content.length() > 500 ? content.substring(0, 500) + "…" : content;
        }
        String first = extractSummaryFromContent(content);
        return (first != null && !first.isBlank()) ? first : "";
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