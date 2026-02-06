package com.sparkLab.study.service;

import com.sparkLab.study.dto.notification.NotificationResponse;
import com.sparkLab.study.entity.Account;
import com.sparkLab.study.entity.Assignment;
import com.sparkLab.study.entity.Feedback;
import com.sparkLab.study.entity.Mentee;
import com.sparkLab.study.entity.Notification;
import com.sparkLab.study.entity.Planner;
import com.sparkLab.study.entity.TodoItem;
import com.sparkLab.study.repository.AssignmentRepository;
import com.sparkLab.study.repository.AssignmentSubmissionRepository;
import com.sparkLab.study.repository.NotificationRepository;
import com.sparkLab.study.repository.PlannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String TYPE_TODO_CREATED = "TODO_CREATED";
    private static final String TYPE_ASSIGNMENT_DUE_SOON = "ASSIGNMENT_DUE_SOON";
    private static final String TYPE_ASSIGNMENT_FEEDBACK = "ASSIGNMENT_FEEDBACK";
    private static final String TYPE_PLANNER_FEEDBACK = "PLANNER_FEEDBACK";

    private static final String LINK_PLANNER = "PLANNER";
    private static final String LINK_ASSIGNMENT = "ASSIGNMENT";
    private static final String TITLE_TODO_CREATED =
            "📌 새로운 할 일이 등록되었어요.\n플래너에서 바로 확인해보세요!";
    private static final String TITLE_ASSIGNMENT_DUE_SOON =
            "⏰ 과제 제출 마감이 내일이에요!\n잊지 말고 오늘 안에 한 번 더 체크해봐요.";
    private static final String TITLE_ASSIGNMENT_FEEDBACK =
            "💬 멘토님이 과제에 피드백을 남겼어요.\n지금 바로 확인해보세요!";
    private static final String TITLE_PLANNER_FEEDBACK =
            "📝 멘토님이 플래너에 피드백을 남겼어요.\n확인하고 다음 계획을 세워보세요!";

    private final NotificationRepository notificationRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final PlannerRepository plannerRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> listByAccount(Long accountId) {
        return notificationRepository.findByRecipient_AccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void notifyNewTodo(TodoItem todoItem) {
        Mentee mentee = todoItem.getMentee();
        if (mentee == null) {
            return;
        }
        Account recipient = mentee.getAccount();
        if (recipient == null) {
            return;
        }
        Planner planner = todoItem.getPlanner();
        Long plannerId = planner == null ? null : planner.getPlannerId();
        createNotification(recipient, TYPE_TODO_CREATED, TITLE_TODO_CREATED, LINK_PLANNER, plannerId);
    }

    @Transactional
    public void notifyFeedback(Feedback feedback) {
        Mentee mentee = feedback.getMentee();
        if (mentee == null || mentee.getAccount() == null) {
            return;
        }
        TodoItem todoItem = feedback.getTodoItem();
        if (todoItem != null && todoItem.getAssignments() != null && !todoItem.getAssignments().isEmpty()) {
            Assignment assignment = todoItem.getAssignments().get(0);
            createNotification(
                    mentee.getAccount(),
                    TYPE_ASSIGNMENT_FEEDBACK,
                    TITLE_ASSIGNMENT_FEEDBACK,
                    LINK_ASSIGNMENT,
                    assignment.getAssignmentId()
            );
            return;
        }
        Long plannerId = resolvePlannerId(mentee.getMenteeId(), feedback.getTargetDate(), todoItem);
        createNotification(
                mentee.getAccount(),
                TYPE_PLANNER_FEEDBACK,
                TITLE_PLANNER_FEEDBACK,
                LINK_PLANNER,
                plannerId
        );
    }

    @Scheduled(cron = "${app.notification.assignment-due.cron:0 0 9 * * *}")
    @Transactional
    public void notifyAssignmentsDueTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Assignment> assignments = assignmentRepository.findByTodoItem_TargetDate(tomorrow);
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        for (Assignment assignment : assignments) {
            TodoItem todoItem = assignment.getTodoItem();
            if (todoItem == null || todoItem.getMentee() == null) {
                continue;
            }
            Mentee mentee = todoItem.getMentee();
            Account recipient = mentee.getAccount();
            if (recipient == null) {
                continue;
            }
            boolean submitted = submissionRepository.existsByAssignment_AssignmentIdAndMentee_MenteeId(
                    assignment.getAssignmentId(),
                    mentee.getMenteeId()
            );
            if (submitted) {
                continue;
            }
            boolean alreadyNotified = notificationRepository
                    .existsByRecipient_AccountIdAndTypeAndLinkTypeAndLinkIdAndCreatedAtBetween(
                            recipient.getAccountId(),
                            TYPE_ASSIGNMENT_DUE_SOON,
                            LINK_ASSIGNMENT,
                            assignment.getAssignmentId(),
                            start,
                            end
                    );
            if (alreadyNotified) {
                continue;
            }
            createNotification(
                    recipient,
                    TYPE_ASSIGNMENT_DUE_SOON,
                    TITLE_ASSIGNMENT_DUE_SOON,
                    LINK_ASSIGNMENT,
                    assignment.getAssignmentId()
            );
        }
    }

    private Long resolvePlannerId(Long menteeId, LocalDateTime targetDate, TodoItem todoItem) {
        if (todoItem != null && todoItem.getPlanner() != null) {
            return todoItem.getPlanner().getPlannerId();
        }
        if (targetDate == null) {
            return null;
        }
        return plannerRepository.findByMentee_MenteeIdAndPlanDate(menteeId, targetDate.toLocalDate())
                .map(Planner::getPlannerId)
                .orElse(null);
    }

    private void createNotification(
            Account recipient,
            String type,
            String title,
            String linkType,
            Long linkId
    ) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .linkType(linkType)
                .linkId(linkId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .type(notification.getType())
                .title(notification.getTitle())
                .linkType(notification.getLinkType())
                .linkId(notification.getLinkId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
