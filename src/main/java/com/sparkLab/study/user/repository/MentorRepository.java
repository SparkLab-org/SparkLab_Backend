package com.sparkLab.study.user.repository;

import com.sparkLab.study.user.entity.Mentor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    Optional<Mentor> findMentorIdByAccount_AccountId(String accountId);

    Optional<Mentor> findByAccount_AccountId(String accountId);

    @Query("SELECT COUNT(m) FROM Mentee m WHERE m.mentor.mentorId = :mentorId")
    int countMentees(@Param("mentorId") Long mentorId);

}
