package com.sparkLab.study.planner.service;


import com.sparkLab.study.common.exception.NotOwnerException;
import com.sparkLab.study.common.exception.ParentResourceNotFoundException;
import com.sparkLab.study.planner.dto.DailyCommentReq;
import com.sparkLab.study.planner.dto.DailyCommentRes;
import com.sparkLab.study.planner.dto.DailyPlanCreateReq;
import com.sparkLab.study.planner.dto.DailyPlanCreateRes;
import com.sparkLab.study.user.entity.Mentee;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.sparkLab.study.planner.entity.DailyPlan;
import com.sparkLab.study.planner.repository.DailyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyPlanService {

    private final DailyPlanRepository dailyPlanRepository;
    private final ModelMapper modelMapper;

    // 요청 응답 캡슐화 - 확장성 고려
    @Transactional
    public DailyPlanCreateRes findOrCreate(DailyPlanCreateReq req, Long menteeId){

        Optional<Long> entityId = dailyPlanRepository.findIdByMenteeIdAndPlanDate(menteeId, req.getPlanDate());
        // 기존 조회
        if(entityId.isPresent()){
            return DailyPlanCreateRes.builder()
                    .dailyPlanId(entityId.get())
                    .build();
        }
        // 새로 생성
        DailyPlan dailyPlan = modelMapper.map(req, DailyPlan.class);
        dailyPlan.assignMentee(menteeId);
        dailyPlanRepository.save(dailyPlan);

        return DailyPlanCreateRes.builder()
                .dailyPlanId(dailyPlan.getDailyPlanId())
                .created(true)
                .build();
    }

    // 선언적 접근 제어, 즉시 권한 검증 예외처리
    @Transactional
    // 보안과 도메인 분리보다 조회 최소 쿼리 우선시, 이미 menteeId 자체가 해당 도메인에 맞게 설계되었음.
    public DailyCommentRes updateComment(DailyCommentReq req, Long menteeId, Long dailyPlanId) {

        DailyPlan dailyPlan = dailyPlanRepository.findById(dailyPlanId)
                .orElseThrow(() -> new ParentResourceNotFoundException("DailyPlan", dailyPlanId));

        if (!dailyPlan.getMentee().getMenteeId().equals(menteeId)) {
            throw new NotOwnerException("DailyPlan", dailyPlan.getDailyPlanId());
        }

        dailyPlan.updateComment(req.getComment());

        dailyPlanRepository.save(dailyPlan);

        return DailyCommentRes.builder()
                .dailyPlanId(dailyPlan.getDailyPlanId())
                .comment(dailyPlan.getComment())
                .build();
    }
}
