package com.sparkLab.study.controller;

import com.sparkLab.study.dto.feedback.FeedbackDraftRequest;
import com.sparkLab.study.dto.feedback.FeedbackDraftResponse;
import com.sparkLab.study.service.FeedbackDraftService;
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
