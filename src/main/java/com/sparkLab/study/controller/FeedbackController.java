package com.sparkLab.study.controller;

import com.sparkLab.study.dto.feedback.FeedbackCreateRequest;
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
@RequestMapping("/domain")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/feedbacks")
    public ResponseEntity<FeedbackResponse> create(@RequestBody @Valid FeedbackCreateRequest request) {
        FeedbackResponse created = feedbackService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/feedbacks")
    public List<FeedbackResponse> list(
            @RequestParam(required = false) Long menteeId,
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) Long todoItemId) {
        return feedbackService.list(menteeId, mentorId, todoItemId);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/feedbacks/{feedbackId}")
    public FeedbackResponse getOne(@PathVariable Long feedbackId) {
        return feedbackService.getOne(feedbackId);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/feedbacks/{feedbackId}")
    public FeedbackResponse update(
            @PathVariable Long feedbackId,
            @RequestBody FeedbackUpdateRequest request) {
        return feedbackService.update(feedbackId, request);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/feedbacks/{feedbackId}")
    public ResponseEntity<Void> delete(@PathVariable Long feedbackId) {
        feedbackService.delete(feedbackId);
        return ResponseEntity.noContent().build();
    }
}
