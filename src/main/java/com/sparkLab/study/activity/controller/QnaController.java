package com.sparkLab.study.activity.controller;

import com.sparkLab.study.activity.dto.qna.*;
import com.sparkLab.study.activity.service.QnaService;
import com.sparkLab.study.common.constant.Subject;
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
public class QnaController {

    private final QnaService qnaService;
    private final UserService menteeService;
    private final UserService mentorService;

    @PreAuthorize("hasRole('MENTEE')")
    @PostMapping
    public ResponseEntity<QnaResponse> create(@RequestBody @Valid QnaCreateRequest request,
                                              @AuthenticationPrincipal Jwt jwt) {
        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        QnaResponse created = qnaService.create(request, menteeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping
    public List<QnaListResponse> list(
            @RequestParam(required = false) Subject subject,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @AuthenticationPrincipal Jwt jwt) {
        Long menteeId = null;
        Long mentorId = null;
        String role = resolveRole(jwt);
        if (AccountRole.MENTEE.name().equals(role)) {
            menteeId = menteeService.accountToUser(jwt.getSubject());
        } else if (AccountRole.MENTOR.name().equals(role)) {
            mentorId = mentorService.accountToUser(jwt.getSubject());
        }
        return qnaService.list(menteeId, mentorId, subject, sort);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{qnaId}")
    public QnaResponse getOne(@PathVariable Long qnaId, @AuthenticationPrincipal Jwt jwt) {
        Long menteeId = null;
        Long mentorId = null;
        String role = resolveRole(jwt);
        if (AccountRole.MENTEE.name().equals(role)) {
            menteeId = menteeService.accountToUser(jwt.getSubject());
        } else if (AccountRole.MENTOR.name().equals(role)) {
            mentorId = mentorService.accountToUser(jwt.getSubject());
        }
        return qnaService.getOne(qnaId, menteeId, mentorId);
    }

    @PreAuthorize("hasRole('MENTEE')")
    @PutMapping("/{qnaId}")
    public QnaResponse update(@PathVariable Long qnaId,
                              @RequestBody QnaUpdateRequest request,
                              @AuthenticationPrincipal Jwt jwt) {
        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        return qnaService.update(qnaId, request, menteeId);
    }

    @PreAuthorize("hasRole('MENTEE')")
    @DeleteMapping("/{qnaId}")
    public ResponseEntity<Void> delete(@PathVariable Long qnaId, @AuthenticationPrincipal Jwt jwt) {
        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        qnaService.delete(qnaId, menteeId);
        return ResponseEntity.noContent().build();
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
