package com.sparkLab.study.repository;

import com.sparkLab.study.entity.Mentor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Optional<Mentor> findByAccount_AccountId(String accountId);
}
