package com.sparkLab.study.task.repository;

import com.sparkLab.study.task.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    boolean existsByAssignment_AssignmentIdAndMentee_MenteeId(Long assignmentId, Long menteeId);
}
