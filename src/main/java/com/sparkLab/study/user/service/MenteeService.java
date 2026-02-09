package com.sparkLab.study.user.service;

import com.sparkLab.study.common.service.UserService;
import com.sparkLab.study.planner.exception.PlannerResourceNotFoundException;
import com.sparkLab.study.user.dto.MenteeActiveLevelResponse;
import com.sparkLab.study.user.dto.MenteeActiveLevelUpdateRequest;
import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.exception.UserNotFoundException;
import com.sparkLab.study.user.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenteeService implements UserService {

    private final MenteeRepository menteeRepository;

    @Cacheable(
            cacheNames = "accountToMentee",
            key = "#accountId"
    )
    @Transactional(readOnly = true)
    public Long accountToUser(String accountId) {

        return menteeRepository.findMenteeIdByAccount_AccountId(accountId)
                .orElseThrow(UserNotFoundException::new).getMenteeId();
    }


    @Transactional
    public MenteeActiveLevelResponse updateActiveLevelByMentor(Long mentorId, Long menteeId, MenteeActiveLevelUpdateRequest request) {
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + menteeId));
        if (mentee.getMentorId() == null || !mentee.getMentorId().getMentorId().equals(mentorId)) {
            throw new PlannerResourceNotFoundException("해당 멘티는 해당 멘토 소속이 아닙니다.");
        }
        mentee.setActiveLevel(request.getActiveLevel());
        menteeRepository.save(mentee);
        return MenteeActiveLevelResponse.builder()
                .menteeId(mentee.getMenteeId())
                .activeLevel(mentee.getActiveLevel())
                .build();
    }
}
