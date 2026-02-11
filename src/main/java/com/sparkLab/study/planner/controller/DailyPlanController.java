package com.sparkLab.study.planner.controller;

import com.sparkLab.study.planner.dto.dailyPlan.*;
import com.sparkLab.study.planner.service.DailyPlanService;
import com.sparkLab.study.user.service.MenteeService;
import com.sparkLab.study.user.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dailyPlan")
public class DailyPlanController {

    private final DailyPlanService dailyPlanService;
    private final MentorService mentorService;
    private final MenteeService menteeService;

    // ===== 멘티용 기본 기능 =====

    /**
     * POST /dailyPlan - 멘티: 자신의 일정 생성 또는 조회
     */
    @PostMapping
    @PreAuthorize("hasRole('MENTEE')")
    public ResponseEntity<DailyPlanCreateRes> findOrCreate(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody DailyPlanCreateReq req) {

        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        return createDailyPlanResponse(dailyPlanService.findOrCreate(req, menteeId));
    }

    /**
     * PUT /dailyPlan/{dailyPlanId}/comment - 멘티: 일정 코멘트 수정
     */
    @PutMapping("/{dailyPlanId}/comment")
    @PreAuthorize("hasRole('MENTEE')")
    public ResponseEntity<DailyCommentRes> updateComment(
            @PathVariable Long dailyPlanId,
            @RequestBody DailyCommentReq req,
            @AuthenticationPrincipal Jwt jwt) {

        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        DailyCommentRes res = dailyPlanService.updateComment(req, menteeId, dailyPlanId);
        return ResponseEntity.ok(res);
    }

    /**
     * GET /dailyPlan - 멘티: 날짜 범위 일정 조회
     */
    @GetMapping
    @PreAuthorize("hasRole('MENTEE')")
    public ResponseEntity<List<DailyPlanRes>> getDailyPlans(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal Jwt jwt) {

        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        return ResponseEntity.ok(dailyPlanService.findByDateRange(menteeId, startDate, endDate));
    }

    /**
     * GET /dailyPlan/today - 멘티: 오늘 일정 조회
     */
    @GetMapping("/today")
    @PreAuthorize("hasRole('MENTEE')")
    public ResponseEntity<?> getTodayPlan(@AuthenticationPrincipal Jwt jwt) {
        Long menteeId = menteeService.accountToUser(jwt.getSubject());
        List<?> res = dailyPlanService.findByDateRange(menteeId, LocalDate.now(), LocalDate.now());

        return res.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(res.get(0));
    }


    // ===== 멘토용 기능 =====

    /**
     * POST /dailyPlan/mentees/{menteeId} - 멘토: 특정 멘티의 일정 생성/조회
     */
    @PostMapping("/mentees/{menteeId}")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<DailyPlanCreateRes> findOrCreateForMentee(
            @PathVariable Long menteeId,
            @RequestBody DailyPlanCreateReq req) {

        return createDailyPlanResponse(dailyPlanService.findOrCreate(req, menteeId));
    }

    /**
     * GET /dailyPlan/mentees/{menteeId} - 멘토: 특정 멘티의 날짜 범위 일정 조회
     */
    @GetMapping("/mentees/{menteeId}")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<List<?>> getDailyPlansForMentee(
            @PathVariable Long menteeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(dailyPlanService.findByDateRange(menteeId, startDate, endDate));
    }

    /**
     * DailyPlanCreateRes에 따라 적절한 응답 반환
     */
    private ResponseEntity<DailyPlanCreateRes> createDailyPlanResponse(DailyPlanCreateRes res) {
        if (res.isCreated()) {
            return ResponseEntity.created(URI.create("/dailyPlan/%d".formatted(res.getDailyPlanId())))
                    .body(res);
        } else {
            return ResponseEntity.ok(res);
        }
    }
}