package com.sparkLab.study.activity.controller;

import com.sparkLab.study.activity.dto.qna.QnaReplyCreateRequest;
import com.sparkLab.study.activity.dto.qna.QnaReplyResponse;
import com.sparkLab.study.activity.service.QnaReplyService;
import com.sparkLab.study.common.service.UserService;
import com.sparkLab.study.security.auth.constant.AccountRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QnaReplyController {

    private final QnaReplyService qnaReplyService;
    private final UserService mentorService;
    private final UserService menteeService;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/{qnaId}/replies")
    public ResponseEntity<QnaReplyResponse> create(@PathVariable Long qnaId,
                                                   @RequestBody @Valid QnaReplyCreateRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        Long mentorId = mentorService.accountToUser(jwt.getSubject());
        QnaReplyResponse created = qnaReplyService.create(qnaId, request, mentorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{qnaId}/replies")
    public List<QnaReplyResponse> list(@PathVariable Long qnaId, @AuthenticationPrincipal Jwt jwt) {
        Long menteeId = null;
        Long mentorId = null;
        String role = resolveRole(jwt);
        if (AccountRole.MENTEE.name().equals(role)) {
            menteeId = menteeService.accountToUser(jwt.getSubject());
        } else if (AccountRole.MENTOR.name().equals(role)) {
            mentorId = mentorService.accountToUser(jwt.getSubject());
        }
        return qnaReplyService.list(qnaId, menteeId, mentorId);
    }

    private static String resolveRole(Jwt jwt) {
        List<String> rolesClaim = jwt.getClaimAsStringList("roles");
        String role = (rolesClaim != null && !rolesClaim.isEmpty()) ? rolesClaim.get(0) : "";
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        return role;
    }
}
