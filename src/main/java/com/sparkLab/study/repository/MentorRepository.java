package com.sparkLab.study.repository;

import com.sparkLab.study.entity.Mentor;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
}
