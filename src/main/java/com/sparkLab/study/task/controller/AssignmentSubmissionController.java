package com.sparkLab.study.task.controller;

import com.sparkLab.study.task.dto.AssignmentSubmissionResponse;
import com.sparkLab.study.task.service.AssignmentSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("assignments")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService submissionService;

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping(value = "/{assignmentId}/submissions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentSubmissionResponse> submit(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file) {
        AssignmentSubmissionResponse response = submissionService.submit(assignmentId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
