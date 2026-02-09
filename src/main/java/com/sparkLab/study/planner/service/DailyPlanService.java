package com.sparkLab.study.planner.service;


import com.sparkLab.study.common.exception.NotOwnerException;
import com.sparkLab.study.common.exception.ParentResourceNotFoundException;
import com.sparkLab.study.planner.dto.DailyCommentDto;
import com.sparkLab.study.planner.dto.DailyPlanDto;
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
    public DailyPlanDto.Res findOrCreate(DailyPlanDto.Req req){

        Optional<Long> entityId = dailyPlanRepository.findIdByMenteeIdAndPlanDate(req.getMenteeId(), req.getPlanDate());
        // 기존 조회
        if(entityId.isPresent()){
            return DailyPlanDto.Res.builder()
                    .dailyPlanId(entityId.get())
                    .build();
        }
        // 새로 생성
        DailyPlan dailyPlan = modelMapper.map(req, DailyPlan.class);
        Long dailyPlanId = dailyPlanRepository.save(dailyPlan).getDailyPlanId();
        return DailyPlanDto.Res.builder()
                .dailyPlanId(dailyPlanId)
                .created(true)
                .build();
    }

    // 선언적 접근 제어, 즉시 권한 검증 예외처리
    @Transactional
    // 보안과 도메인 분리보다 조회 최소 쿼리 우선시, 이미 menteeId 자체가 해당 도메인에 맞게 설계되었음.
    public DailyCommentDto.Res updateComment(DailyCommentDto.Req req) {

        DailyPlan dailyPlan = dailyPlanRepository.findById(req.getDailyPlanId())
                .orElseThrow(() -> new ParentResourceNotFoundException("DailyPlan", req.getDailyPlanId()));

        if (!dailyPlan.getMentee().getMenteeId().equals(req.getMenteeId())) {
            throw new NotOwnerException("DailyPlan", dailyPlan.getDailyPlanId());
        }

        dailyPlan.updateComment(req.getComment());
        // dailyPlanRepository.save(dailyPlan);

        return DailyCommentDto.Res.builder()
                .dailyPlanId(dailyPlan.getDailyPlanId())
                .comment(dailyPlan.getComment())
                .build();
    }
}
