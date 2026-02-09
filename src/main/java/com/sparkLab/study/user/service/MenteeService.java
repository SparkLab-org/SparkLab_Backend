package com.sparkLab.study.user.service;

import com.sparkLab.study.common.service.UserService;
import com.sparkLab.study.planner.exception.PlannerResourceNotFoundException;
import com.sparkLab.study.user.dto.MenteeActiveLevelResponse;
import com.sparkLab.study.user.dto.MenteeActiveLevelUpdateRequest;
import com.sparkLab.study.user.dto.MenteeSummaryResponse;
import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.entity.Mentor;
import com.sparkLab.study.user.exception.UserNotFoundException;
import com.sparkLab.study.user.repository.MenteeRepository;
import com.sparkLab.study.user.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenteeService implements UserService{

    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;

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

    @Transactional(readOnly = true)
    public List<MenteeSummaryResponse> listMenteesByMentorAccount(String accountId) {
        Mentor mentor = mentorRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() -> new PlannerResourceNotFoundException("멘토를 찾을 수 없습니다. accountId=" + accountId));
        return menteeRepository.findByMentorId_MentorId(mentor.getMentorId()).stream()
                .map(mentee -> MenteeSummaryResponse.builder()
                        .menteeId(mentee.getMenteeId())
                        .accountId(mentee.getAccount() != null ? mentee.getAccount().getAccountId() : null)
                        .activeLevel(mentee.getActiveLevel())
                        .build())
                .toList();
    }
}
