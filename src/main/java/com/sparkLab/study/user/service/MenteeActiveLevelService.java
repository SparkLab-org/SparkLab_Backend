package com.sparkLab.study.user.service;

import com.sparkLab.study.user.constant.ActiveLevel;
import com.sparkLab.study.activity.service.NotificationService;
import com.sparkLab.study.task.entity.Assignment;
import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.task.repository.AssignmentRepository;
import com.sparkLab.study.task.repository.AssignmentSubmissionRepository;
import com.sparkLab.study.user.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenteeActiveLevelService {

    private final MenteeRepository menteeRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "${app.active-level.cron:0 5 0 * * *}")
    @Transactional
    public void updateActiveLevelsByMissingSubmissions() {
        LocalDate today = LocalDate.now();
        List<Mentee> mentees = menteeRepository.findAll();
        for (Mentee mentee : mentees) {
            int missedStreak = calculateMissedStreak(mentee.getMenteeId(), today);
            ActiveLevel autoLevel = resolveAutoLevel(missedStreak);
            if (autoLevel != null && isHigher(autoLevel, mentee.getActiveLevel())) {
                mentee.setActiveLevel(autoLevel);
                notificationService.notifyMentorActiveLevelChanged(mentee, autoLevel);
            }
        }
    }

    private int calculateMissedStreak(Long menteeId, LocalDate today) {
        List<Assignment> assignments = assignmentRepository
                .findByTodoItem_Mentee_MenteeIdAndTodoItem_TargetDateBeforeOrderByTodoItem_TargetDateAscAssignmentIdAsc(
                        menteeId,
                        today
                );
        int streak = 0;
        for (Assignment assignment : assignments) {
            boolean submitted = submissionRepository.existsByAssignment_AssignmentIdAndMentee_MenteeId(
                    assignment.getAssignmentId(),
                    menteeId
            );
            if (submitted) {
                streak = 0;
            } else {
                streak++;
            }
        }
        return streak;
    }

    private ActiveLevel resolveAutoLevel(int missedStreak) {
        if (missedStreak >= 3) {
            return ActiveLevel.DANGER;
        }
        if (missedStreak >= 2) {
            return ActiveLevel.WARNING;
        }
        return null;
    }

    private boolean isHigher(ActiveLevel candidate, ActiveLevel current) {
        return rank(candidate) > rank(current);
    }

    private int rank(ActiveLevel level) {
        if (level == null) {
            return 0;
        }
        return switch (level) {
            case NORMAL -> 1;
            case WARNING -> 2;
            case DANGER -> 3;
        };
    }
}
