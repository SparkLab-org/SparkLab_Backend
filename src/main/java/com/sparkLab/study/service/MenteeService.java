package com.sparkLab.study.service;

import com.sparkLab.study.dto.mentee.MenteeActiveLevelResponse;
import com.sparkLab.study.dto.mentee.MenteeActiveLevelUpdateRequest;
import com.sparkLab.study.entity.Mentee;
import com.sparkLab.study.exception.PlannerResourceNotFoundException;
import com.sparkLab.study.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenteeService {

    private final MenteeRepository menteeRepository;

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
