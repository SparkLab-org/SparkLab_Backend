package com.sparkLab.study.controller;

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
@RequestMapping("feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping
    public ResponseEntity<FeedbackResponse> create(@RequestBody @Valid FeedbackCreateRequest request) {
        FeedbackResponse created = feedbackService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping
    public List<FeedbackResponse> list(
            @RequestParam(required = false) Long menteeId,
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) Long todoItemId,
            @RequestParam(required = false) com.sparkLab.study.constant.Subject subject) {
        return feedbackService.list(menteeId, mentorId, todoItemId, subject);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{feedbackId}")
    public FeedbackResponse getOne(@PathVariable Long feedbackId) {
        return feedbackService.getOne(feedbackId);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{feedbackId}")
    public FeedbackResponse update(
            @PathVariable Long feedbackId,
            @RequestBody FeedbackUpdateRequest request) {
        return feedbackService.update(feedbackId, request);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{feedbackId}/important")
    public FeedbackResponse updateImportantComment(
            @PathVariable Long feedbackId,
            @RequestBody FeedbackImportantUpdateRequest request) {
        return feedbackService.updateImportantComment(feedbackId, request.getImportantComment());
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{feedbackId}/important")
    public ResponseEntity<FeedbackResponse> deleteImportantComment(
            @PathVariable Long feedbackId) {
        FeedbackResponse response = feedbackService.deleteImportantComment(feedbackId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> delete(@PathVariable Long feedbackId) {
        feedbackService.delete(feedbackId);
        return ResponseEntity.noContent().build();
    }
}
