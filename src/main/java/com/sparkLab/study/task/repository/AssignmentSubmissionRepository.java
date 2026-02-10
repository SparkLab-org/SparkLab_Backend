package com.sparkLab.study.task.repository;

import com.sparkLab.study.task.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    boolean existsByAssignment_AssignmentIdAndMentee_MenteeId(Long assignmentId, Long menteeId);

    /** 과제별 제출 목록 (최신순) */
    List<AssignmentSubmission> findByAssignment_AssignmentIdOrderByCreateTimeDesc(Long assignmentId);
}
