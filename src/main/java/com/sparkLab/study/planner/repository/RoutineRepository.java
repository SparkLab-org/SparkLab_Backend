package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findByMentee_MenteeIdAndIsActiveTrue(Long menteeId);
}
