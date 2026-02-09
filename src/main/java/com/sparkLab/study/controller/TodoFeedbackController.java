package com.sparkLab.study.controller;

import com.sparkLab.study.constant.Subject;
import com.sparkLab.study.dto.feedback.FeedbackCreateRequest;
import com.sparkLab.study.dto.feedback.FeedbackImportantUpdateRequest;
import com.sparkLab.study.dto.feedback.FeedbackResponse;
import com.sparkLab.study.dto.feedback.FeedbackUpdateRequest;
import com.sparkLab.study.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("todos")
@RequiredArgsConstructor
public class TodoFeedbackController {

    private final FeedbackService feedbackService;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/{todoItemId}/feedbacks")
    public ResponseEntity<FeedbackResponse> createForTodo(
            @PathVariable Long todoItemId,
            @RequestBody @Valid FeedbackCreateRequest request) {
        FeedbackResponse created = feedbackService.createForTodo(todoItemId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{todoItemId}/feedbacks")
    public List<FeedbackResponse> listForTodo(
            @PathVariable Long todoItemId,
            @RequestParam(required = false) Subject subject) {
        return feedbackService.listByTodo(todoItemId, subject);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{todoItemId}/feedbacks/{feedbackId}")
    public FeedbackResponse getForTodo(
            @PathVariable Long todoItemId,
            @PathVariable Long feedbackId) {
        return feedbackService.getForTodo(todoItemId, feedbackId);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{todoItemId}/feedbacks/{feedbackId}")
    public FeedbackResponse updateForTodo(
            @PathVariable Long todoItemId,
            @PathVariable Long feedbackId,
            @RequestBody FeedbackUpdateRequest request) {
        return feedbackService.updateForTodo(todoItemId, feedbackId, request);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{todoItemId}/feedbacks/{feedbackId}/important")
    public FeedbackResponse updateImportantForTodo(
            @PathVariable Long todoItemId,
            @PathVariable Long feedbackId,
            @RequestBody FeedbackImportantUpdateRequest request) {
        return feedbackService.updateImportantForTodo(todoItemId, feedbackId, request.getImportantComment());
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{todoItemId}/feedbacks/{feedbackId}/important")
    public ResponseEntity<FeedbackResponse> deleteImportantForTodo(
            @PathVariable Long todoItemId,
            @PathVariable Long feedbackId) {
        FeedbackResponse response = feedbackService.deleteImportantForTodo(todoItemId, feedbackId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{todoItemId}/feedbacks/{feedbackId}")
    public ResponseEntity<Void> deleteForTodo(
            @PathVariable Long todoItemId,
            @PathVariable Long feedbackId) {
        feedbackService.deleteForTodo(todoItemId, feedbackId);
        return ResponseEntity.noContent().build();
    }
}
