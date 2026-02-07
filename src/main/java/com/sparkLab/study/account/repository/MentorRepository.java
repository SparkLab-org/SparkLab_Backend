package com.sparkLab.study.account.repository;

import com.sparkLab.study.account.entity.Mentor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Optional<Mentor> findByAccount_AccountId(String accountId);
}
