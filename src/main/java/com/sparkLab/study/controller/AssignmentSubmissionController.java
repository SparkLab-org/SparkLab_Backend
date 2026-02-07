package com.sparkLab.study.controller;

import com.sparkLab.study.dto.assignment.AssignmentSubmissionBatchResponse;
import com.sparkLab.study.service.AssignmentSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("assignments")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService submissionService;

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping(value = "/{assignmentId}/submissions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentSubmissionBatchResponse> submit(
            @PathVariable Long assignmentId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "comment", required = false) String comment) {
        AssignmentSubmissionBatchResponse response = submissionService.submit(assignmentId, file, files, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
