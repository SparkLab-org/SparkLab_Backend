package com.sparkLab.study.controller;

import com.sparkLab.study.dto.feedback.FeedbackCommentCreateRequest;
import com.sparkLab.study.dto.feedback.FeedbackCommentResponse;
import com.sparkLab.study.service.FeedbackCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("feedbacks")
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
}
