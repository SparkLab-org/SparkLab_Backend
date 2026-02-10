package com.sparkLab.study.user.service;

import com.sparkLab.study.user.dto.MenteeMyPageRes;
import com.sparkLab.study.user.dto.MentorMyPageRes;
import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.entity.Mentor;
import com.sparkLab.study.user.repository.MenteeRepository;
import com.sparkLab.study.user.repository.MentorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;

    public MenteeMyPageRes getMenteeMyPage(String accountId) {

        Mentee mentee = menteeRepository.findByAccount_AccountId(accountId)
                .orElseThrow();

        Object[] stats = menteeRepository.todoStats(mentee.getMenteeId());
        long total = (Long) stats[0];
        long completed = stats[1] != null ? (Long) stats[1] : 0;

        double rate = total > 0 ? completed * 100.0 / total : 0.0;

        Mentor mentor = mentee.getMentor();

        return new MenteeMyPageRes(
                mentee.getMenteeId(),
                mentee.getAccount().getAccountId(),
                mentee.getActiveLevel(),
                mentor.getMentorId(),
                mentor.getAccount().getAccountId(),
                mentor.getSubject(),
                (int) total,
                (int) completed,
                rate
        );
    }

    public MentorMyPageRes getMentorMyPage(String accountId) {

        Mentor mentor = mentorRepository.findByAccount_AccountId(accountId)
                .orElseThrow();

        int menteeCount = mentorRepository.countMentees(mentor.getMentorId());

        return new MentorMyPageRes(
                mentor.getMentorId(),
                mentor.getAccount().getAccountId(),
                mentor.getSubject(),
                menteeCount
        );
    }
}

