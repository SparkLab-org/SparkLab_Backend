package com.sparkLab.study.repository;

import com.sparkLab.study.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    boolean existsByAssignment_AssignmentIdAndMentee_MenteeId(Long assignmentId, Long menteeId);
}
