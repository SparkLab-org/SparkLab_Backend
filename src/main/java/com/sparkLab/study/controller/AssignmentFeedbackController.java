package com.sparkLab.study.controller;

import com.sparkLab.study.constant.Subject;
import com.sparkLab.study.dto.feedback.FeedbackCreateRequest;
import com.sparkLab.study.dto.feedback.FeedbackResponse;
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
}
