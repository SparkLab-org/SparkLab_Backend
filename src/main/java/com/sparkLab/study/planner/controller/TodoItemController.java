package com.sparkLab.study.planner.controller;

import com.sparkLab.study.planner.service.TodoItemService;
import com.sparkLab.study.planner.dto.todo.MenteeTodosResponse;
import com.sparkLab.study.planner.dto.todo.TodoItemCreateRequest;
import com.sparkLab.study.planner.dto.todo.TodoItemResponse;
import com.sparkLab.study.planner.dto.todo.TodoItemUpdateRequest;
import com.sparkLab.study.security.auth.constant.AccountRole;
import com.sparkLab.study.user.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoItemController {

    private final TodoItemService todoItemService;
    private final MentorService mentorService;

    /**
     * 할일 목록 조회
     * - plannerId: 해당 플래너의 할일 (flat)
     * - 멘토: menteeId, planDate 조합으로 멘티별 조회
     *   - menteeId: 멘티 필터 (없으면 전체 멘티)
     *   - planDate: 날짜 필터 (없으면 전체 기간)
     * - 멘티 + planDate: 해당 날짜 할일 (flat)
     */
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping
    public Object list(
            @RequestParam(required = false) Long plannerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate,
            @RequestParam(required = false) Long menteeId,
            @AuthenticationPrincipal Jwt jwt) {
        if (plannerId != null) {
            return todoItemService.listByPlannerId(plannerId);
        }
        List<String> rolesClaim = jwt.getClaimAsStringList("roles");
        String role = (rolesClaim != null && !rolesClaim.isEmpty()) ? rolesClaim.get(0) : "";
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        if (AccountRole.MENTOR.name().equals(role)) {
            Long mentorId = mentorService.accountToUser(jwt.getSubject());
            return todoItemService.listByMentorGroupedByMentee(mentorId, planDate, menteeId);
        }
        if (planDate != null) {
            return todoItemService.listByPlanDate(planDate);
        }
        throw new IllegalArgumentException("plannerId 또는 planDate 중 하나는 필수입니다.");
    }

    /**
     * 멘토: 멘티별·날짜별 할일 조회 (별도 엔드포인트)
     * GET /todos/mentor?menteeId=1&planDate=2026-02-10
     */
    @PreAuthorize("hasRole('MENTOR')")
    @GetMapping("/mentor")
    public List<MenteeTodosResponse> listByMentor(
            @RequestParam(required = false) Long menteeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate,
            @AuthenticationPrincipal Jwt jwt) {
        Long mentorId = mentorService.accountToUser(jwt.getSubject());
        return todoItemService.listByMentorGroupedByMentee(mentorId, planDate, menteeId);
    }

    //할일 상세 조회
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/{todoItemId}")
    public TodoItemResponse getOne(@PathVariable Long todoItemId) {
        return todoItemService.getOne(todoItemId);
    }

    // 할일 추가 (수정 , 삭제 가능)
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping
    public ResponseEntity<TodoItemResponse> create(
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 고정 할일 배정
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping("/fixed")
    public ResponseEntity<TodoItemResponse> createFixed(
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.createFixed(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 할일 수정 (고정 할일이면 403)
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PutMapping("/{todoItemId}")
    public TodoItemResponse update(
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.update(todoItemId, request);
    }

    // 고정 할일 수정
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PutMapping("/fixed/{todoItemId}")
    public TodoItemResponse updateFixed(
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.updateFixed(todoItemId, request);
    }

    // 할일 삭제 (고정 할일이면 403)
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @DeleteMapping("/{todoItemId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long todoItemId) {
        todoItemService.delete(todoItemId);
        return ResponseEntity.noContent().build();
    }

    // 고정 할일 삭제
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @DeleteMapping("/fixed/{todoItemId}")
    public ResponseEntity<Void> deleteFixed(
            @PathVariable Long todoItemId) {
        todoItemService.deleteFixed(todoItemId);
        return ResponseEntity.noContent().build();
    }
}