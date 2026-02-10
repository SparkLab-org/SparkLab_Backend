package com.sparkLab.study.task.service;

import com.sparkLab.study.common.constant.Subject;
import com.sparkLab.study.planner.exception.PlannerResourceNotFoundException;
import com.sparkLab.study.task.dto.assignment.AssignmentResponse;
import com.sparkLab.study.task.dto.assignment.MenteeAssignmentsResponse;
import com.sparkLab.study.task.dto.assignment.AssignmentSubmissionResponse;
import com.sparkLab.study.task.entity.Assignment;
import com.sparkLab.study.task.entity.AssignmentSubmission;
import com.sparkLab.study.task.exception.TaskResourceNotFoundException;
import com.sparkLab.study.task.repository.AssignmentRepository;
import com.sparkLab.study.task.repository.AssignmentSubmissionRepository;
import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final MenteeRepository menteeRepository;

    /**
     * 멘토: 전체 멘티의 과제를 멘티별로 묶어서 조회 (제출/미제출 상태 포함)
     */
    @Transactional(readOnly = true)
    public List<MenteeAssignmentsResponse> listAllByMentorGroupedByMentee(Long mentorId) {
        List<Mentee> mentees = menteeRepository.findByMentor_MentorId(mentorId);
        return mentees.stream()
                .map(mentee -> {
                    List<Assignment> assignments = assignmentRepository
                            .findByTodoItem_Mentee_MenteeIdOrderByTodoItem_TargetDateDescCreateTimeDesc(mentee.getMenteeId());
                    List<AssignmentResponse> assignmentResponses = assignments.stream()
                            .map(a -> toAssignmentResponse(a, mentee.getMenteeId()))
                            .collect(Collectors.toList());
                    return MenteeAssignmentsResponse.builder()
                            .menteeId(mentee.getMenteeId())
                            .accountId(mentee.getAccount() != null ? mentee.getAccount().getAccountId() : null)
                            .activeLevel(mentee.getActiveLevel())
                            .assignments(assignmentResponses)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 멘토: 특정 멘티의 과제를 1개 그룹으로 조회 (제출/미제출 상태 포함)
     */
    @Transactional(readOnly = true)
    public List<MenteeAssignmentsResponse> listByMenteeForMentorAsGroup(Long menteeId, Long mentorId) {
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + menteeId));
        if (mentee.getMentor() == null || !mentee.getMentor().getMentorId().equals(mentorId)) {
            throw new PlannerResourceNotFoundException("해당 멘티는 해당 멘토 소속이 아닙니다.");
        }
        List<Assignment> assignments = assignmentRepository
                .findByTodoItem_Mentee_MenteeIdOrderByTodoItem_TargetDateDescCreateTimeDesc(menteeId);

        List<AssignmentResponse> assignmentResponses = assignments.stream()
                .map(a -> toAssignmentResponse(a, menteeId))
                .collect(Collectors.toList());

        return List.of(MenteeAssignmentsResponse.builder()
                .menteeId(mentee.getMenteeId())
                .accountId(mentee.getAccount() != null ? mentee.getAccount().getAccountId() : null)
                .activeLevel(mentee.getActiveLevel())
                .assignments(assignmentResponses)
                .build());
    }

    /**
     * 과제별 제출 목록 조회 (멘토·멘티 공통, 본인 소속만)
     */
    @Transactional(readOnly = true)
    public List<AssignmentSubmissionResponse> listSubmissionsByAssignmentId(
            Long assignmentId,
            Long mentorId,
            Long menteeId
    ) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new TaskResourceNotFoundException("과제를 찾을 수 없습니다. assignmentId=" + assignmentId));

        Long targetMenteeId = assignment.getTodoItem() != null && assignment.getTodoItem().getMentee() != null
                ? assignment.getTodoItem().getMentee().getMenteeId()
                : null;
        if (targetMenteeId == null) {
            throw new TaskResourceNotFoundException("과제에 연결된 멘티가 없습니다. assignmentId=" + assignmentId);
        }

        // 멘토: assignment.mentor == mentorId 또는 mentee.mentor == mentorId
        // 멘티: assignment의 mentee == menteeId
        boolean allowed = false;
        if (mentorId != null) {
            if (assignment.getMentor() != null && assignment.getMentor().getMentorId().equals(mentorId)) {
                allowed = true;
            } else if (assignment.getTodoItem().getMentee().getMentor() != null
                    && assignment.getTodoItem().getMentee().getMentor().getMentorId().equals(mentorId)) {
                allowed = true;
            }
        }
        if (menteeId != null && targetMenteeId.equals(menteeId)) {
            allowed = true;
        }
        if (!allowed) {
            throw new TaskResourceNotFoundException("해당 과제에 대한 조회 권한이 없습니다. assignmentId=" + assignmentId);
        }

        List<AssignmentSubmission> submissions = submissionRepository
                .findByAssignment_AssignmentIdOrderByCreateTimeDesc(assignmentId);
        return submissions.stream()
                .map(this::toSubmissionResponse)
                .collect(Collectors.toList());
    }

    private AssignmentResponse toAssignmentResponse(Assignment a, Long menteeId) {
        boolean submitted = submissionRepository.existsByAssignment_AssignmentIdAndMentee_MenteeId(
                a.getAssignmentId(), menteeId);

        Long latestSubmissionId = null;
        if (submitted) {
            List<AssignmentSubmission> subs = submissionRepository
                    .findByAssignment_AssignmentIdOrderByCreateTimeDesc(a.getAssignmentId());
            if (!subs.isEmpty()) {
                latestSubmissionId = subs.get(0).getSubmissionId();
            }
        }

        Subject subject = a.getTodoItem() != null ? a.getTodoItem().getSubject() : null;

        return AssignmentResponse.builder()
                .assignmentId(a.getAssignmentId())
                .todoItemId(a.getTodoItem() != null ? a.getTodoItem().getTodoItemId() : null)
                .menteeId(menteeId)
                .materialTitle(a.getMaterialTitle())
                .subject(subject)
                .targetDate(a.getTodoItem() != null ? a.getTodoItem().getTargetDate() : null)
                .createTime(a.getCreateTime())
                .submitted(submitted)
                .latestSubmissionId(latestSubmissionId)
                .build();
    }

    private AssignmentSubmissionResponse toSubmissionResponse(AssignmentSubmission s) {
        return AssignmentSubmissionResponse.builder()
                .submissionId(s.getSubmissionId())
                .assignmentId(s.getAssignment().getAssignmentId())
                .menteeId(s.getMentee() != null ? s.getMentee().getMenteeId() : null)
                .imageUrl(s.getImageUrl())
                .comment(s.getComment())
                .status(s.getStatus())
                .createTime(s.getCreateTime())
                .build();
    }
}
