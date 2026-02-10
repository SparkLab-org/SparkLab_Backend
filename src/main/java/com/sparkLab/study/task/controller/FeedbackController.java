package com.sparkLab.study.task.controller;

import com.sparkLab.study.common.service.UserService;
import com.sparkLab.study.security.auth.constant.AccountRole;
import com.sparkLab.study.task.service.FeedbackService;
import com.sparkLab.study.task.dto.feedback.FeedbackCreateRequest;
import com.sparkLab.study.task.dto.feedback.FeedbackResponse;
import com.sparkLab.study.task.dto.feedback.FeedbackUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.sparkLab.study.common.constant.Subject;
import com.sparkLab.study.task.dto.feedback.TodoFeedbackStatusResponse;


@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserService mentorService;
    private final UserService menteeService;


    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping
    public ResponseEntity<FeedbackResponse> create(@RequestBody @Valid FeedbackCreateRequest request) {
        FeedbackResponse created = feedbackService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping
    public List<FeedbackResponse> list(
            @RequestParam(required = false) Long todoItemId,
            @RequestParam(required = false) Boolean isImportant,
            @RequestParam(required = false) LocalDateTime targetDate,
            @RequestParam(required = false) Subject subject,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @AuthenticationPrincipal Jwt jwt) {
        List<String> rolesClaim = jwt.getClaimAsStringList("roles");
        String role = (rolesClaim != null && !rolesClaim.isEmpty()) ? rolesClaim.get(0) : "";
        if (role != null && role.startsWith("ROLE_")) role = role.substring(5);
        Long menteeId = null;
        Long mentorId = null;
        if (AccountRole.MENTEE.name().equals(role)) {
            menteeId = menteeService.accountToUser(jwt.getSubject());
        } else if (AccountRole.MENTOR.name().equals(role)) {
            mentorId = mentorService.accountToUser(jwt.getSubject());
        }
        return feedbackService.list(menteeId, mentorId, todoItemId, isImportant, targetDate, subject, sort);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{feedbackId}")
    public FeedbackResponse getOne(@PathVariable Long feedbackId, @AuthenticationPrincipal Jwt jwt) {
        List<String> rolesClaim = jwt.getClaimAsStringList("roles");
        String role = (rolesClaim != null && !rolesClaim.isEmpty()) ? rolesClaim.get(0) : "";
        if (role != null && role.startsWith("ROLE_")) role = role.substring(5);
        Long currentMenteeId = AccountRole.MENTEE.name().equals(role) ? menteeService.accountToUser(jwt.getSubject()) : null;
        return feedbackService.getOne(feedbackId, currentMenteeId);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{feedbackId}")
    public FeedbackResponse update(
            @PathVariable Long feedbackId,
            @RequestBody FeedbackUpdateRequest request) {
        return feedbackService.update(feedbackId, request);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> delete(@PathVariable Long feedbackId) {
        feedbackService.delete(feedbackId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PatchMapping("/{feedbackId}/important")
    public FeedbackResponse updateImportant(@PathVariable Long feedbackId, @RequestBody java.util.Map<String, Boolean> body) {
        Boolean isImportant = body != null ? body.get("isImportant") : null;
        return feedbackService.updateImportant(feedbackId, isImportant != null && isImportant);
    }

    @PreAuthorize("hasRole('MENTEE')")
    @PostMapping("/{feedbackId}/bookmark")
    public ResponseEntity<Void> addBookmark(@PathVariable Long feedbackId, @AuthenticationPrincipal Jwt jwt) {
        List<String> rolesClaim = jwt.getClaimAsStringList("roles");
        String role = (rolesClaim != null && !rolesClaim.isEmpty()) ? rolesClaim.get(0) : "";
        if (!AccountRole.MENTEE.name().equals(role)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }
        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        feedbackService.addBookmark(menteeId, feedbackId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MENTEE')")
    @DeleteMapping("/{feedbackId}/bookmark")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long feedbackId, @AuthenticationPrincipal Jwt jwt) {
        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        feedbackService.removeBookmark(menteeId, feedbackId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/todo-status")
    public List<TodoFeedbackStatusResponse> getTodoStatus(
            @RequestParam Long menteeId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate planDate) {
        return feedbackService.getTodoStatus(menteeId, planDate);
    }
}
