package com.sparkLab.study.account.repository;

import com.sparkLab.study.account.entity.Mentee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {
    Optional<Mentee> findByAccount_AccountId(String accountId);
}
