package com.sparkLab.study.task.controller;

import com.sparkLab.study.task.service.FeedbackService;
import com.sparkLab.study.task.dto.feedback.FeedbackCreateRequest;
import com.sparkLab.study.task.dto.feedback.FeedbackResponse;
import com.sparkLab.study.task.dto.feedback.FeedbackUpdateRequest;
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
            @RequestParam(required = false) Long todoItemId) {
        return feedbackService.list(menteeId, mentorId, todoItemId);
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
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> delete(@PathVariable Long feedbackId) {
        feedbackService.delete(feedbackId);
        return ResponseEntity.noContent().build();
    }
}
