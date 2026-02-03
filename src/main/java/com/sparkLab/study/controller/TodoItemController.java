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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TodoItemController {

    private final TodoItemService todoItemService;

    // 멘티 기준 API

    // 할일 목록: plannerId 또는 planDate 중 하나로 조회
    @GetMapping("/mentees/{menteeId}/todos")
    public List<TodoItemResponse> list(
            @PathVariable Long menteeId,
            @RequestParam(required = false) Long plannerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate) {
        if (plannerId != null) {
            return todoItemService.listByPlannerId(plannerId);
        }
        if (planDate != null) {
            return todoItemService.listByMenteeAndDate(menteeId, planDate);
        }
        throw new IllegalArgumentException("plannerId 또는 planDate 중 하나는 필수입니다.");
    }

    // 할일 단건 조회
    @GetMapping("/mentees/{menteeId}/todos/{todoItemId}")
    public TodoItemResponse getOne(@PathVariable Long menteeId, @PathVariable Long todoItemId) {
        return todoItemService.getOne(menteeId, todoItemId);
    }

    // 멘티가 할일 추가 (수정/삭제 가능)
    @PostMapping("/mentees/{menteeId}/todos")
    public ResponseEntity<TodoItemResponse> createByMentee(
            @PathVariable Long menteeId,
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.createByMentee(menteeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 멘티가 할일 수정 (고정 할일이면 403)
    @PutMapping("/mentees/{menteeId}/todos/{todoItemId}")
    public TodoItemResponse updateByMentee(
            @PathVariable Long menteeId,
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.updateByMentee(menteeId, todoItemId, request);
    }

    // 멘티가 할일 삭제 (고정 할일이면 403)
    @DeleteMapping("/mentees/{menteeId}/todos/{todoItemId}")
    public ResponseEntity<Void> deleteByMentee(
            @PathVariable Long menteeId,
            @PathVariable Long todoItemId) {
        todoItemService.deleteByMentee(menteeId, todoItemId);
        return ResponseEntity.noContent().build();
    }

    // 멘토 기준 API

    // 멘토가 멘티 할일 추가
    @PostMapping("/mentors/{mentorId}/mentees/{menteeId}/todos")
    public ResponseEntity<TodoItemResponse> createByMentor(
            @PathVariable Long mentorId,
            @PathVariable Long menteeId,
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.createByMentor(mentorId, menteeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 할 일 수정
    @PutMapping("/mentors/{mentorId}/mentees/{menteeId}/todos/{todoItemId}")
    public TodoItemResponse updateByMentor(
            @PathVariable Long mentorId,
            @PathVariable Long menteeId,
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.updateByMentor(mentorId, menteeId, todoItemId, request);
    }

    // 할 일 삭제
    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}/todos/{todoItemId}")
    public ResponseEntity<Void> deleteByMentor(
            @PathVariable Long mentorId,
            @PathVariable Long menteeId,
            @PathVariable Long todoItemId) {
        todoItemService.deleteByMentor(mentorId, menteeId, todoItemId);
        return ResponseEntity.noContent().build();
    }
}
