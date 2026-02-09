package com.sparkLab.study.task.controller;

import com.sparkLab.study.task.dto.assignment.AssignmentSubmissionBatchResponse;
import com.sparkLab.study.task.dto.assignment.AssignmentSubmissionResponse;
import com.sparkLab.study.task.service.AssignmentSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService submissionService;

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

    @PutMapping(value = "/{assignmentId}/submissions/{submissionId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentSubmissionResponse> update(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "comment", required = false) String comment) {
        AssignmentSubmissionResponse response = submissionService.update(assignmentId, submissionId, file, comment);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{assignmentId}/submissions/{submissionId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId) {
        submissionService.delete(assignmentId, submissionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{assignmentId}/submissions/{submissionId}/comment")
    public ResponseEntity<AssignmentSubmissionResponse> deleteComment(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId) {
        AssignmentSubmissionResponse response = submissionService.deleteComment(assignmentId, submissionId);
        return ResponseEntity.ok(response);
    }
}
