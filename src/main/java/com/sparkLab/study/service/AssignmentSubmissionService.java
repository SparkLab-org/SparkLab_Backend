package com.sparkLab.study.service;

import com.sparkLab.study.dto.assignment.AssignmentSubmissionResponse;
import com.sparkLab.study.entity.Assignment;
import com.sparkLab.study.entity.AssignmentSubmission;
import com.sparkLab.study.exception.ResourceNotFoundException;
import com.sparkLab.study.repository.AssignmentRepository;
import com.sparkLab.study.repository.AssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "gif"
    );

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final NotificationService notificationService;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public AssignmentSubmissionResponse submit(Long assignmentId, MultipartFile file) {
        validateFile(file);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("과제를 찾을 수 없습니다. assignmentId=" + assignmentId));
        if (assignment.getTodoItem() == null || assignment.getTodoItem().getMentee() == null) {
            throw new ResourceNotFoundException("과제에 연결된 멘티가 없습니다. assignmentId=" + assignmentId);
        }
        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        Path uploadPath = Paths.get(uploadDir, "assignments", String.valueOf(assignmentId));
        createDirectories(uploadPath);

        Path targetPath = uploadPath.resolve(filename).normalize();
        try {
            Files.copy(file.getInputStream(), targetPath);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 저장에 실패했습니다.");
        }

        String imageUrl = "/uploads/assignments/" + assignmentId + "/" + filename;
        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assignment(assignment)
                .mentee(assignment.getTodoItem().getMentee())
                .imageUrl(imageUrl)
                .status("SUBMITTED")
                .build();
        submission = submissionRepository.save(submission);
        notificationService.notifyMentorAssignmentSubmitted(submission);
        return toResponse(submission);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("제출 파일은 필수입니다.");
        }
        String contentType = file.getContentType();
        String extension = getExtension(file.getOriginalFilename());
        if ((contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType))
                && (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension))) {
            throw new IllegalArgumentException("이미지(jpg/png/gif) 파일만 업로드할 수 있습니다.");
        }
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    private void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("업로드 경로를 생성할 수 없습니다.");
        }
    }

    private AssignmentSubmissionResponse toResponse(AssignmentSubmission submission) {
        return AssignmentSubmissionResponse.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(submission.getAssignment().getAssignmentId())
                .menteeId(submission.getMentee().getMenteeId())
                .imageUrl(submission.getImageUrl())
                .status(submission.getStatus())
                .createTime(submission.getCreateTime())
                .build();
    }
}
