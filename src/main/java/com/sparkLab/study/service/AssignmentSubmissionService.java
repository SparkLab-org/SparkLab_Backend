package com.sparkLab.study.service;

import com.sparkLab.study.dto.assignment.AssignmentSubmissionBatchResponse;
import com.sparkLab.study.dto.assignment.AssignmentSubmissionResponse;
import com.sparkLab.study.entity.Assignment;
import com.sparkLab.study.entity.AssignmentSubmission;
import com.sparkLab.study.exception.TaskResourceNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
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
    public AssignmentSubmissionBatchResponse submit(
            Long assignmentId,
            MultipartFile file,
            List<MultipartFile> files,
            String comment
    ) {
        List<MultipartFile> uploadTargets = normalizeFiles(file, files);
        validateFiles(uploadTargets);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new TaskResourceNotFoundException("과제를 찾을 수 없습니다. assignmentId=" + assignmentId));
        if (assignment.getTodoItem() == null || assignment.getTodoItem().getMentee() == null) {
            throw new TaskResourceNotFoundException("과제에 연결된 멘티가 없습니다. assignmentId=" + assignmentId);
        }
        List<AssignmentSubmissionResponse> responses = new ArrayList<>();
        Path uploadPath = Paths.get(uploadDir, "assignments", String.valueOf(assignmentId));
        createDirectories(uploadPath);

        for (MultipartFile uploadFile : uploadTargets) {
            String extension = getExtension(uploadFile.getOriginalFilename());
            String filename = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
            Path targetPath = uploadPath.resolve(filename).normalize();
            try {
                Files.copy(uploadFile.getInputStream(), targetPath);
            } catch (IOException e) {
                throw new IllegalArgumentException("파일 저장에 실패했습니다.");
            }

            String imageUrl = "/uploads/assignments/" + assignmentId + "/" + filename;
            AssignmentSubmission submission = AssignmentSubmission.builder()
                    .assignment(assignment)
                    .mentee(assignment.getTodoItem().getMentee())
                    .imageUrl(imageUrl)
                    .comment(comment)
                    .status("SUBMITTED")
                    .build();
            submission = submissionRepository.save(submission);
            notificationService.notifyMentorAssignmentSubmitted(submission);
            responses.add(toResponse(submission));
        }

        return AssignmentSubmissionBatchResponse.builder()
                .submissions(responses)
                .build();
    }

    @Transactional
    public AssignmentSubmissionResponse update(
            Long assignmentId,
            Long submissionId,
            MultipartFile file,
            String comment
    ) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new TaskResourceNotFoundException("제출을 찾을 수 없습니다. submissionId=" + submissionId));
        if (!submission.getAssignment().getAssignmentId().equals(assignmentId)) {
            throw new TaskResourceNotFoundException("과제와 제출 정보가 일치하지 않습니다. assignmentId=" + assignmentId);
        }
        if ((file == null || file.isEmpty()) && comment == null) {
            throw new IllegalArgumentException("수정할 내용이 없습니다.");
        }
        if (file != null && !file.isEmpty()) {
            validateFiles(List.of(file));
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
            deleteExistingFile(submission.getImageUrl(), assignmentId);
            submission.setImageUrl("/uploads/assignments/" + assignmentId + "/" + filename);
        }
        if (comment != null) {
            String normalized = comment.trim();
            submission.setComment(normalized.isEmpty() ? null : normalized);
        }
        submission = submissionRepository.save(submission);
        return toResponse(submission);
    }

    @Transactional
    public void delete(Long assignmentId, Long submissionId) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new TaskResourceNotFoundException("제출을 찾을 수 없습니다. submissionId=" + submissionId));
        if (!submission.getAssignment().getAssignmentId().equals(assignmentId)) {
            throw new TaskResourceNotFoundException("과제와 제출 정보가 일치하지 않습니다. assignmentId=" + assignmentId);
        }
        deleteExistingFile(submission.getImageUrl(), assignmentId);
        submissionRepository.delete(submission);
    }

    @Transactional
    public AssignmentSubmissionResponse deleteComment(Long assignmentId, Long submissionId) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new TaskResourceNotFoundException("제출을 찾을 수 없습니다. submissionId=" + submissionId));
        if (!submission.getAssignment().getAssignmentId().equals(assignmentId)) {
            throw new TaskResourceNotFoundException("과제와 제출 정보가 일치하지 않습니다. assignmentId=" + assignmentId);
        }
        submission.setComment(null);
        submission = submissionRepository.save(submission);
        return toResponse(submission);
    }

    private List<MultipartFile> normalizeFiles(MultipartFile file, List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            return files;
        }
        List<MultipartFile> result = new ArrayList<>();
        if (file != null) {
            result.add(file);
        }
        return result;
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("제출 파일은 필수입니다.");
        }
        for (MultipartFile file : files) {
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

    private void deleteExistingFile(String imageUrl, Long assignmentId) {
        String filename = extractFilename(imageUrl);
        if (filename == null) {
            return;
        }
        Path path = Paths.get(uploadDir, "assignments", String.valueOf(assignmentId), filename);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("기존 파일 삭제에 실패했습니다.");
        }
    }

    private String extractFilename(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return null;
        }
        int lastSlash = imageUrl.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == imageUrl.length() - 1) {
            return null;
        }
        return imageUrl.substring(lastSlash + 1);
    }

    private AssignmentSubmissionResponse toResponse(AssignmentSubmission submission) {
        return AssignmentSubmissionResponse.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(submission.getAssignment().getAssignmentId())
                .menteeId(submission.getMentee().getMenteeId())
                .imageUrl(submission.getImageUrl())
                .comment(submission.getComment())
                .status(submission.getStatus())
                .createTime(submission.getCreateTime())
                .build();
    }
}
