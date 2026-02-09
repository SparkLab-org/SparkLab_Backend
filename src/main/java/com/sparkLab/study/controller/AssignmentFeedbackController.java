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
@RequestMapping("assignments")
@RequiredArgsConstructor
public class AssignmentFeedbackController {

    private final FeedbackService feedbackService;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/{assignmentId}/feedbacks")
    public ResponseEntity<FeedbackResponse> createForAssignment(
            @PathVariable Long assignmentId,
            @RequestBody @Valid FeedbackCreateRequest request) {
        FeedbackResponse created = feedbackService.createForAssignment(assignmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{assignmentId}/feedbacks")
    public List<FeedbackResponse> listForAssignment(
            @PathVariable Long assignmentId,
            @RequestParam(required = false) Subject subject) {
        return feedbackService.listByAssignment(assignmentId, subject);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{assignmentId}/feedbacks/{feedbackId}")
    public FeedbackResponse getForAssignment(
            @PathVariable Long assignmentId,
            @PathVariable Long feedbackId) {
        return feedbackService.getForAssignment(assignmentId, feedbackId);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{assignmentId}/feedbacks/{feedbackId}")
    public FeedbackResponse updateForAssignment(
            @PathVariable Long assignmentId,
            @PathVariable Long feedbackId,
            @RequestBody FeedbackUpdateRequest request) {
        return feedbackService.updateForAssignment(assignmentId, feedbackId, request);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{assignmentId}/feedbacks/{feedbackId}/important")
    public FeedbackResponse updateImportantForAssignment(
            @PathVariable Long assignmentId,
            @PathVariable Long feedbackId,
            @RequestBody FeedbackImportantUpdateRequest request) {
        return feedbackService.updateImportantForAssignment(assignmentId, feedbackId, request.getImportantComment());
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{assignmentId}/feedbacks/{feedbackId}/important")
    public ResponseEntity<FeedbackResponse> deleteImportantForAssignment(
            @PathVariable Long assignmentId,
            @PathVariable Long feedbackId) {
        FeedbackResponse response = feedbackService.deleteImportantForAssignment(assignmentId, feedbackId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{assignmentId}/feedbacks/{feedbackId}")
    public ResponseEntity<Void> deleteForAssignment(
            @PathVariable Long assignmentId,
            @PathVariable Long feedbackId) {
        feedbackService.deleteForAssignment(assignmentId, feedbackId);
        return ResponseEntity.noContent().build();
    }
}
