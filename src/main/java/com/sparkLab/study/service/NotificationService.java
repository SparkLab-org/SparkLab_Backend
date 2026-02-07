package com.sparkLab.study.service;

import com.sparkLab.study.constant.ActiveLevel;
import com.sparkLab.study.constant.NotificationLinkType;
import com.sparkLab.study.dto.notification.NotificationResponse;
import com.sparkLab.study.security.auth.entity.Account;
import com.sparkLab.study.entity.Assignment;
import com.sparkLab.study.entity.AssignmentSubmission;
import com.sparkLab.study.entity.Feedback;
import com.sparkLab.study.entity.Mentee;
import com.sparkLab.study.entity.Mentor;
import com.sparkLab.study.entity.Notification;
import com.sparkLab.study.entity.TodoItem;
import com.sparkLab.study.repository.AssignmentRepository;
import com.sparkLab.study.repository.AssignmentSubmissionRepository;
import com.sparkLab.study.repository.NotificationRepository;
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
    private static final String TYPE_MENTOR_ASSIGNMENT_SUBMITTED = "MENTOR_ASSIGNMENT_SUBMITTED";
    private static final String TYPE_MENTOR_QUESTION_CREATED = "MENTOR_QUESTION_CREATED";
    private static final String TYPE_MENTOR_ASSIGNMENT_MISSED = "MENTOR_ASSIGNMENT_MISSED";
    private static final String TYPE_MENTEE_WARNING = "MENTEE_WARNING";
    private static final String TYPE_MENTEE_DANGER = "MENTEE_DANGER";

    private static final NotificationLinkType LINK_TODO = NotificationLinkType.TODO;
    private static final NotificationLinkType LINK_ASSIGNMENT = NotificationLinkType.ASSIGNMENT;
    private static final NotificationLinkType LINK_FEEDBACK = NotificationLinkType.FEEDBACK;
    private static final NotificationLinkType LINK_QUESTION = NotificationLinkType.QUESTION;
    private static final NotificationLinkType LINK_MENTEE = NotificationLinkType.MENTEE;
    private static final String TITLE_TODO_CREATED =
            "📌 새로운 할 일이 등록되었어요.\n플래너에서 바로 확인해보세요!";
    private static final String TITLE_ASSIGNMENT_DUE_SOON =
            "⏰ 과제 제출 마감이 내일이에요!\n잊지 말고 오늘 안에 한 번 더 체크해봐요.";
    private static final String TITLE_ASSIGNMENT_FEEDBACK =
            "💬 멘토님이 과제에 피드백을 남겼어요.\n지금 바로 확인해보세요!";
    private static final String TITLE_PLANNER_FEEDBACK =
            "📝 멘토님이 플래너에 피드백을 남겼어요.\n확인하고 다음 계획을 세워보세요!";
    private static final String TITLE_MENTOR_ASSIGNMENT_SUBMITTED =
            "📥 %s 멘티가 과제를 제출했어요.\n피드백을 작성해 주세요.";
    private static final String TITLE_MENTOR_QUESTION_CREATED =
            "❓ %s 멘티가 질문을 남겼어요.\n확인 후 답변을 작성해 주세요.";
    private static final String TITLE_MENTOR_ASSIGNMENT_MISSED =
            "⚠️ %s 멘티가 과제를 제출하지 않았어요.\n진행 상황을 확인해 주세요.";
    private static final String TITLE_MENTEE_WARNING =
            "⚠️ %s 멘티의 과제 미제출이 2회 누적되어 등급이 Warning으로 변경되었어요.";
    private static final String TITLE_MENTEE_DANGER =
            "🚨 %s 멘티의 과제 미제출이 3회 누적되어 등급이 Danger로 변경되었어요.";

    private final NotificationRepository notificationRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> listByAccount(String accountId) {
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
        createNotification(
                recipient,
                TYPE_TODO_CREATED,
                TITLE_TODO_CREATED,
                LINK_TODO,
                todoItem.getTodoItemId()
        );
    }

    @Transactional
    public void notifyFeedback(Feedback feedback) {
        Mentee mentee = feedback.getMentee();
        if (mentee == null || mentee.getAccount() == null) {
            return;
        }
        TodoItem todoItem = feedback.getTodoItem();
        if (todoItem != null && todoItem.getAssignments() != null && !todoItem.getAssignments().isEmpty()) {
            createNotification(
                    mentee.getAccount(),
                    TYPE_ASSIGNMENT_FEEDBACK,
                    TITLE_ASSIGNMENT_FEEDBACK,
                    LINK_FEEDBACK,
                    feedback.getFeedbackId()
            );
            return;
        }
        createNotification(
                mentee.getAccount(),
                TYPE_PLANNER_FEEDBACK,
                TITLE_PLANNER_FEEDBACK,
                LINK_FEEDBACK,
                feedback.getFeedbackId()
        );
    }

    @Transactional
    public void notifyMentorAssignmentSubmitted(AssignmentSubmission submission) {
        if (submission == null || submission.getAssignment() == null) {
            return;
        }
        Assignment assignment = submission.getAssignment();
        TodoItem todoItem = assignment.getTodoItem();
        Mentee mentee = todoItem == null ? null : todoItem.getMentee();
        Account recipient = resolveMentorAccount(assignment, mentee);
        if (recipient == null) {
            return;
        }
        createNotification(
                recipient,
                TYPE_MENTOR_ASSIGNMENT_SUBMITTED,
                formatMenteeTitle(TITLE_MENTOR_ASSIGNMENT_SUBMITTED, mentee),
                LINK_ASSIGNMENT,
                assignment.getAssignmentId()
        );
    }

    @Transactional
    public void notifyMentorQuestionCreated(Mentee mentee, Long qnaId) {
        Account recipient = resolveMentorAccount(null, mentee);
        if (recipient == null) {
            return;
        }
        createNotification(
                recipient,
                TYPE_MENTOR_QUESTION_CREATED,
                formatMenteeTitle(TITLE_MENTOR_QUESTION_CREATED, mentee),
                LINK_QUESTION,
                qnaId
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

    @Scheduled(cron = "${app.notification.assignment-missed.cron:0 0 9 * * *}")
    @Transactional
    public void notifyMissedAssignments() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Assignment> assignments = assignmentRepository.findByTodoItem_TargetDate(yesterday);
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        for (Assignment assignment : assignments) {
            TodoItem todoItem = assignment.getTodoItem();
            if (todoItem == null || todoItem.getMentee() == null) {
                continue;
            }
            Mentee mentee = todoItem.getMentee();
            boolean submitted = submissionRepository.existsByAssignment_AssignmentIdAndMentee_MenteeId(
                    assignment.getAssignmentId(),
                    mentee.getMenteeId()
            );
            if (submitted) {
                continue;
            }
            Account recipient = resolveMentorAccount(assignment, mentee);
            if (recipient == null) {
                continue;
            }
            boolean alreadyNotified = notificationRepository
                    .existsByRecipient_AccountIdAndTypeAndLinkTypeAndLinkIdAndCreatedAtBetween(
                            recipient.getAccountId(),
                            TYPE_MENTOR_ASSIGNMENT_MISSED,
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
                    TYPE_MENTOR_ASSIGNMENT_MISSED,
                    formatMenteeTitle(TITLE_MENTOR_ASSIGNMENT_MISSED, mentee),
                    LINK_ASSIGNMENT,
                    assignment.getAssignmentId()
            );
        }
    }

    @Transactional
    public void notifyMentorActiveLevelChanged(Mentee mentee, ActiveLevel newLevel) {
        if (mentee == null || newLevel == null) {
            return;
        }
        Account recipient = resolveMentorAccount(null, mentee);
        if (recipient == null) {
            return;
        }
        if (newLevel == ActiveLevel.WARNING) {
            createNotification(
                    recipient,
                    TYPE_MENTEE_WARNING,
                    formatMenteeTitle(TITLE_MENTEE_WARNING, mentee),
                    LINK_MENTEE,
                    mentee.getMenteeId()
            );
            return;
        }
        if (newLevel == ActiveLevel.DANGER) {
            createNotification(
                    recipient,
                    TYPE_MENTEE_DANGER,
                    formatMenteeTitle(TITLE_MENTEE_DANGER, mentee),
                    LINK_MENTEE,
                    mentee.getMenteeId()
            );
        }
    }

    private Account resolveMentorAccount(Assignment assignment, Mentee mentee) {
        Mentor mentor = assignment == null ? null : assignment.getMentor();
        if (mentor == null && mentee != null) {
            mentor = mentee.getMentorId();
        }
        if (mentor == null) {
            return null;
        }
        return mentor.getAccount();
    }

    private String formatMenteeTitle(String template, Mentee mentee) {
        String menteeLabel = mentee == null || mentee.getMenteeId() == null
                ? "알 수 없음"
                : String.valueOf(mentee.getMenteeId());
        return String.format(template, menteeLabel);
    }

    private void createNotification(
            Account recipient,
            String type,
            String title,
            NotificationLinkType linkType,
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
