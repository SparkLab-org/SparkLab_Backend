package com.sparkLab.study.planner.controller;

import com.sparkLab.study.common.service.UserService;
import com.sparkLab.study.planner.dto.dailyPlan.DailyCommentReq;
import com.sparkLab.study.planner.dto.dailyPlan.DailyCommentRes;
import com.sparkLab.study.planner.dto.dailyPlan.DailyPlanCreateReq;
import com.sparkLab.study.planner.dto.dailyPlan.DailyPlanCreateRes;
import com.sparkLab.study.planner.service.DailyPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/dailyPlan")
public class DailyPlanController {

    private final DailyPlanService dailyPlanService;
    private final UserService menteeService;

    // REST 규칙과 프론트 편의성 사이의 타협
    @PostMapping
    public ResponseEntity<DailyPlanCreateRes> findOrCreate(@AuthenticationPrincipal Jwt jwt,
                                                           @RequestBody DailyPlanCreateReq req) {
        // 다형성
        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        // 도메인 경계분리
        DailyPlanCreateRes res = dailyPlanService.findOrCreate(req, menteeId);

        if (res.isCreated()) {
            // 새로 생성 → 201 Created + Location 헤더
            return ResponseEntity.created(URI.create("/dailyPlan/%d".formatted(res.getDailyPlanId())))
                    .body(res);
        } else {
            // 기존 리소스 조회 → 200 OK
            return ResponseEntity.ok(res);
        }
    }

    /**
     * 멘토가 특정 멘티의 DailyPlan을 생성/조회하는 엔드포인트.
     * - path 로 멘티 ID를 직접 전달
     * - 기존 findOrCreate 와 동일하게, 이미 있으면 조회, 없으면 생성
     */
    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/mentees/{menteeId}")
    public ResponseEntity<DailyPlanCreateRes> findOrCreateForMentee(@PathVariable Long menteeId,
                                                                    @RequestBody DailyPlanCreateReq req) {
        DailyPlanCreateRes res = dailyPlanService.findOrCreate(req, menteeId);

        if (res.isCreated()) {
            return ResponseEntity.created(URI.create("/dailyPlan/%d".formatted(res.getDailyPlanId())))
                    .body(res);
        } else {
            return ResponseEntity.ok(res);
        }
    }

    // DailyPlan 리소스에 대한 수정
    @PutMapping("/{dailyPlanId}/comment")
    public ResponseEntity<DailyCommentRes> updateComment(@PathVariable Long dailyPlanId,
                                                         @RequestBody DailyCommentReq req,
                                                         @AuthenticationPrincipal Jwt jwt) {

        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        // 즉시 갱신 용도, ID 기준으로 추가 GET 호출 없이 화면에 반영
        DailyCommentRes res = dailyPlanService.updateComment(req, menteeId, dailyPlanId);
        return ResponseEntity.ok(res);
    }
}
