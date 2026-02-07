package com.sparkLab.study.repository;

import com.sparkLab.study.entity.Mentee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {
    Optional<Mentee> findByAccount_AccountId(Long accountId);
}
