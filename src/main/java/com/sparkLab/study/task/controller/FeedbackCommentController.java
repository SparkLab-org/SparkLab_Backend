package com.sparkLab.study.task.controller;

import com.sparkLab.study.task.dto.feedback.FeedbackCommentCreateRequest;
import com.sparkLab.study.task.dto.feedback.FeedbackCommentResponse;
import com.sparkLab.study.task.dto.feedback.FeedbackCommentUpdateRequest;
import com.sparkLab.study.task.service.FeedbackCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackCommentController {

    private final FeedbackCommentService feedbackCommentService;

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping("/{feedbackId}/comments")
    public ResponseEntity<FeedbackCommentResponse> create(
            @PathVariable Long feedbackId,
            @RequestBody @Valid FeedbackCommentCreateRequest request) {
        FeedbackCommentResponse created = feedbackCommentService.create(feedbackId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{feedbackId}/comments")
    public List<FeedbackCommentResponse> list(@PathVariable Long feedbackId) {
        return feedbackCommentService.list(feedbackId);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PutMapping("/{feedbackId}/comments/{commentId}")
    public ResponseEntity<FeedbackCommentResponse> update(
            @PathVariable Long feedbackId,
            @PathVariable Long commentId,
            @RequestBody FeedbackCommentUpdateRequest request) {
        return ResponseEntity.ok(feedbackCommentService.update(feedbackId, commentId, request));
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @DeleteMapping("/{feedbackId}/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long feedbackId, @PathVariable Long commentId) {
        feedbackCommentService.delete(feedbackId, commentId);
        return ResponseEntity.noContent().build();
    }
}
