package com.sparkLab.study.user.service;

import com.sparkLab.study.common.service.UserService;
import com.sparkLab.study.user.dto.MentorRes;
import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.entity.Mentor;
import com.sparkLab.study.user.exception.UserNotFoundException;
import com.sparkLab.study.user.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorService implements UserService {

    private final MentorRepository mentorRepository;

    @Cacheable(
            cacheNames = "accountToMentor",
            key = "#accountId"
    )
    @Transactional(readOnly = true)
    public Long accountToUser(String accountId) {

        return mentorRepository.findMentorIdByAccount_AccountId(accountId)
                .orElseThrow(UserNotFoundException::new).getMentorId();
    }


    @Transactional(readOnly = true)
    public MentorRes getMentor(String accountId) {

        Mentor mentor = mentorRepository.findByAccount_AccountId(accountId)
                .orElseThrow(UserNotFoundException::new);

        List<Long> menteeIds = mentor.getMentees().stream()
                .map(Mentee::getMenteeId)
                .toList();

        return new MentorRes(
                mentor.getMentorId(),
                mentor.getSubject(),
                menteeIds,
                menteeIds.size()
        );
    }
}
