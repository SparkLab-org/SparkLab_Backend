package com.sparkLab.study.service;

import com.sparkLab.study.dto.todo.TodoItemCreateRequest;
import com.sparkLab.study.dto.todo.TodoItemResponse;
import com.sparkLab.study.dto.todo.TodoItemUpdateRequest;
import com.sparkLab.study.entity.Planner;
import com.sparkLab.study.entity.TodoItem;
import com.sparkLab.study.exception.MentorFixedTodoException;
import com.sparkLab.study.exception.ResourceNotFoundException;
import com.sparkLab.study.repository.PlannerRepository;
import com.sparkLab.study.repository.TodoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;
    private final PlannerRepository plannerRepository;
    private final NotificationService notificationService;

    // 할일 생성
    @Transactional
    public TodoItemResponse create(TodoItemCreateRequest request) {
        return createTodo(request, false);
    }

    // 고정 할일 생성
    @Transactional
    public TodoItemResponse createFixed(TodoItemCreateRequest request) {
        return createTodo(request, true);
    }

    // 플래너 기준 할일 목록 
    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByPlannerId(Long plannerId) {
        return todoItemRepository.findByPlanner_PlannerIdOrderByCreateTimeAsc(plannerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 날짜 기준 할일 목록
    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByPlanDate(LocalDate planDate) {
        return todoItemRepository.findByPlanner_PlanDateOrderByCreateTimeAsc(planDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 할일 단건 조회 
    @Transactional(readOnly = true)
    public TodoItemResponse getOne(Long todoItemId) {
        TodoItem todo = findTodo(todoItemId);
        return toResponse(todo);
    }

    // 할일 수정 (고정 할일이면 403)
    @Transactional
    public TodoItemResponse update(Long todoItemId, TodoItemUpdateRequest request) {
        TodoItem todo = findTodo(todoItemId);
        if (Boolean.TRUE.equals(todo.getIsFixed())) {
            throw new MentorFixedTodoException();
        }
        applyUpdate(todo, request);
        return toResponse(todoItemRepository.save(todo));
    }

    // 고정 할일 수정
    @Transactional
    public TodoItemResponse updateFixed(Long todoItemId, TodoItemUpdateRequest request) {
        TodoItem todo = findTodo(todoItemId);
        todo = todoItemRepository.save(applyUpdate(todo, request));
        return toResponse(todo);
    }

    // 할일 삭제 (고정 할일이면 403)
    @Transactional
    public void delete(Long todoItemId) {
        TodoItem todo = findTodo(todoItemId);
        if (Boolean.TRUE.equals(todo.getIsFixed())) {
            throw new MentorFixedTodoException();
        }
        todoItemRepository.delete(todo);
    }

    // 고정 할일 삭제
    @Transactional
    public void deleteFixed(Long todoItemId) {
        TodoItem todo = findTodo(todoItemId);
        todoItemRepository.delete(todo);
    }

    private TodoItem findTodo(Long todoItemId) {
        return todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new ResourceNotFoundException("할일을 찾을 수 없습니다. todoItemId=" + todoItemId));
    }

    private TodoItemResponse createTodo(TodoItemCreateRequest request, boolean isFixed) {
        Planner planner = plannerRepository.findById(request.getPlannerId())
                .orElseThrow(() -> new ResourceNotFoundException("플래너를 찾을 수 없습니다. plannerId=" + request.getPlannerId()));
        if (planner.getMentee() == null) {
            throw new ResourceNotFoundException("플래너에 연결된 멘티가 없습니다. plannerId=" + request.getPlannerId());
        }
        TodoItem todo = TodoItem.builder()
                .mentee(planner.getMentee())
                .mentor(isFixed ? planner.getMentee().getMentorId() : null)
                .planner(planner)
                .targetDate(planner.getPlanDate())
                .title(request.getTitle())
                .subject(request.getSubject())
                .type(request.getType())
                .isFixed(isFixed)
                .status("TODO")
                .plannedMinutes(request.getPlannedMinutes())
                .build();
        todo = todoItemRepository.save(todo);
        notificationService.notifyNewTodo(todo);
        return toResponse(todo);
    }

    private TodoItem applyUpdate(TodoItem todo, TodoItemUpdateRequest request) {
        if (request.getTitle() != null) todo.setTitle(request.getTitle());
        if (request.getSubject() != null) todo.setSubject(request.getSubject());
        if (request.getType() != null) todo.setType(request.getType());
        if (request.getStatus() != null) todo.setStatus(request.getStatus());
        if (request.getPlannedMinutes() != null) todo.setPlannedMinutes(request.getPlannedMinutes());
        if (request.getActualMinutes() != null) todo.setActualMinutes(request.getActualMinutes());
        if (request.getCompletedAt() != null) todo.setCompletedAt(request.getCompletedAt());
        return todo;
    }

    private TodoItemResponse toResponse(TodoItem todo) {
        return TodoItemResponse.builder()
                .todoItemId(todo.getTodoItemId())
                .plannerId(todo.getPlanner().getPlannerId())
                .targetDate(todo.getTargetDate())
                .title(todo.getTitle())
                .subject(todo.getSubject())
                .type(todo.getType())
                .isFixed(todo.getIsFixed())
                .status(todo.getStatus())
                .plannedMinutes(todo.getPlannedMinutes())
                .actualMinutes(todo.getActualMinutes())
                .completedAt(todo.getCompletedAt())
                .createTime(todo.getCreateTime())
                .updateTime(todo.getUpdateTime())
                .build();
    }
}
