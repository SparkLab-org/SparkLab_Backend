package com.sparkLab.study.repository;

import com.sparkLab.study.entity.Mentee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {
}
