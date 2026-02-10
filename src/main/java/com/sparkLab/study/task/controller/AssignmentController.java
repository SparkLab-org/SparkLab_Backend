package com.sparkLab.study.task.controller;

import com.sparkLab.study.security.auth.constant.AccountRole;
import com.sparkLab.study.task.dto.assignment.AssignmentResponse;
import com.sparkLab.study.task.dto.assignment.AssignmentSubmissionResponse;
import com.sparkLab.study.task.dto.assignment.MenteeAssignmentsResponse;
import com.sparkLab.study.task.service.AssignmentService;
import com.sparkLab.study.user.service.MenteeService;
import com.sparkLab.study.user.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final MentorService mentorService;
    private final MenteeService menteeService;

    /**
     * 멘토: 과제 목록 조회 (멘티별로 구분)
     * - menteeId 없음: 전체 멘티의 과제를 멘티별로 묶어서 반환
     * - menteeId 있음: 해당 멘티의 과제만 1개 그룹으로 반환
     */
    @PreAuthorize("hasRole('MENTOR')")
    @GetMapping
    public List<MenteeAssignmentsResponse> list(
            @RequestParam(required = false) Long menteeId,
            @AuthenticationPrincipal Jwt jwt) {
        Long mentorId = mentorService.accountToUser(jwt.getSubject());
        if (menteeId != null) {
            return assignmentService.listByMenteeForMentorAsGroup(menteeId, mentorId);
        }
        return assignmentService.listAllByMentorGroupedByMentee(mentorId);
    }

    /**
     * 과제별 제출 목록 조회 (멘토·멘티 공통)
     */
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{assignmentId}/submissions")
    public List<AssignmentSubmissionResponse> listSubmissions(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal Jwt jwt) {
        List<String> rolesClaim = jwt.getClaimAsStringList("roles");
        String role = (rolesClaim != null && !rolesClaim.isEmpty()) ? rolesClaim.get(0) : "";
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        Long mentorId = null;
        Long menteeId = null;
        if (AccountRole.MENTOR.name().equals(role)) {
            mentorId = mentorService.accountToUser(jwt.getSubject());
        } else if (AccountRole.MENTEE.name().equals(role)) {
            menteeId = menteeService.accountToUser(jwt.getSubject());
        }
        return assignmentService.listSubmissionsByAssignmentId(assignmentId, mentorId, menteeId);
    }
}
