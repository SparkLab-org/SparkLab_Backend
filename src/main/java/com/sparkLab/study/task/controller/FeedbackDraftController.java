package com.sparkLab.study.task.controller;

import com.sparkLab.study.task.service.FeedbackDraftService;
import com.sparkLab.study.task.dto.feedback.FeedbackDraftRequest;
import com.sparkLab.study.task.dto.feedback.FeedbackDraftResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
public class FeedbackDraftController {

    private final FeedbackDraftService feedbackDraftService;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/drafts")
    public FeedbackDraftResponse generateDraft(@RequestBody(required = false) FeedbackDraftRequest request) {
        return feedbackDraftService.generateDraft(request);
    }
}
