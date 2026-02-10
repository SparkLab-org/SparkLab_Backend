package com.sparkLab.study.user.repository;

import com.sparkLab.study.user.entity.Mentee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {

    Optional<Mentee> findMenteeIdByAccount_AccountId(String accountId);

    Optional<Mentee> findByAccount_AccountId(String accountId);

    List<Mentee> findByMentor_MentorId(Long mentorId);


    @Query("""
        SELECT COUNT(t), 
               SUM(CASE WHEN t.completedAt IS NOT NULL THEN 1 ELSE 0 END)
        FROM TodoItem t
        WHERE t.mentee.menteeId = :menteeId
    """)
    Object[] todoStats(@Param("menteeId") Long menteeId);

}
