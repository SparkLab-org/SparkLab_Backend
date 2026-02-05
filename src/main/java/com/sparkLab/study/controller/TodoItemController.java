package com.sparkLab.study.controller;

import com.sparkLab.study.dto.todo.TodoItemCreateRequest;
import com.sparkLab.study.dto.todo.TodoItemResponse;
import com.sparkLab.study.dto.todo.TodoItemUpdateRequest;
import com.sparkLab.study.service.TodoItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/domain")
@RequiredArgsConstructor
public class TodoItemController {

    private final TodoItemService todoItemService;

    /** 할일 목록: plannerId 또는 planDate 중 하나로 조회 */
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/todos")
    public List<TodoItemResponse> list(
            @RequestParam(required = false) Long plannerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate) {
        if (plannerId != null) {
            return todoItemService.listByPlannerId(plannerId);
        }
        if (planDate != null) {
            return todoItemService.listByPlanDate(planDate);
        }
        throw new IllegalArgumentException("plannerId 또는 planDate 중 하나는 필수입니다.");
    }

    //할일 상세 조회
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/todos/{todoItemId}")
    public TodoItemResponse getOne(@PathVariable Long todoItemId) {
        return todoItemService.getOne(todoItemId);
    }

    // 할일 추가 (수정 , 삭제 가능)
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping("/todos")
    public ResponseEntity<TodoItemResponse> create(
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 고정 할일 배정
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping("/todos/fixed")
    public ResponseEntity<TodoItemResponse> createFixed(
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.createFixed(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 할일 수정 (고정 할일이면 403)
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PutMapping("/todos/{todoItemId}")
    public TodoItemResponse update(
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.update(todoItemId, request);
    }

    // 고정 할일 수정
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PutMapping("/todos/fixed/{todoItemId}")
    public TodoItemResponse updateFixed(
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.updateFixed(todoItemId, request);
    }

    // 할일 삭제 (고정 할일이면 403)
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @DeleteMapping("/todos/{todoItemId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long todoItemId) {
        todoItemService.delete(todoItemId);
        return ResponseEntity.noContent().build();
    }

    // 고정 할일 삭제
    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @DeleteMapping("/todos/fixed/{todoItemId}")
    public ResponseEntity<Void> deleteFixed(
            @PathVariable Long todoItemId) {
        todoItemService.deleteFixed(todoItemId);
        return ResponseEntity.noContent().build();
    }
}
