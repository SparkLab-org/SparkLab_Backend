package com.sparkLab.study.service;

import com.sparkLab.study.dto.todo.TodoItemCreateRequest;
import com.sparkLab.study.dto.todo.TodoItemResponse;
import com.sparkLab.study.dto.todo.TodoItemUpdateRequest;
import com.sparkLab.study.entity.Mentee;
import com.sparkLab.study.entity.Mentor;
import com.sparkLab.study.entity.Planner;
import com.sparkLab.study.entity.TodoItem;
import com.sparkLab.study.exception.MentorFixedTodoException;
import com.sparkLab.study.exception.ResourceNotFoundException;
import com.sparkLab.study.repository.MenteeRepository;
import com.sparkLab.study.repository.MentorRepository;
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
    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;

    // 멘티가 할일 생성
    @Transactional
    public TodoItemResponse createByMentee(Long menteeId, TodoItemCreateRequest request) {
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new ResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + menteeId));
        Planner planner = plannerRepository.findById(request.getPlannerId())
                .orElseThrow(() -> new ResourceNotFoundException("플래너를 찾을 수 없습니다. plannerId=" + request.getPlannerId()));
        if (!planner.getMentee().getMenteeId().equals(menteeId)) {
            throw new ResourceNotFoundException("해당 플래너는 본인 소속이 아닙니다.");
        }
        TodoItem todo = TodoItem.builder()
                .mentee(mentee)
                .mentor(null)
                .planner(planner)
                .targetDate(planner.getPlanDate())
                .title(request.getTitle())
                .subject(request.getSubject())
                .type(request.getType())
                .isFixed(false)
                .status("TODO")
                .plannedMinutes(request.getPlannedMinutes())
                .build();
        todo = todoItemRepository.save(todo);
        return toResponse(todo);
    }

    // 멘토가 멘티 할일 생성
    @Transactional
    public TodoItemResponse createByMentor(Long mentorId, Long menteeId, TodoItemCreateRequest request) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("멘토를 찾을 수 없습니다. mentorId=" + mentorId));
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new ResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + menteeId));
        Planner planner = plannerRepository.findById(request.getPlannerId())
                .orElseThrow(() -> new ResourceNotFoundException("플래너를 찾을 수 없습니다. plannerId=" + request.getPlannerId()));
        if (!planner.getMentee().getMenteeId().equals(menteeId)) {
            throw new ResourceNotFoundException("해당 플래너는 해당 멘티 소속이 아닙니다.");
        }
        TodoItem todo = TodoItem.builder()
                .mentee(mentee)
                .mentor(mentor)
                .planner(planner)
                .targetDate(planner.getPlanDate())
                .title(request.getTitle())
                .subject(request.getSubject())
                .type(request.getType())
                .isFixed(true)
                .status("TODO")
                .plannedMinutes(request.getPlannedMinutes())
                .build();
        todo = todoItemRepository.save(todo);
        return toResponse(todo);
    }

    // 플래너 기준 할일 목록 
    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByPlannerId(Long plannerId) {
        return todoItemRepository.findByPlanner_PlannerIdOrderByCreateTimeAsc(plannerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 멘티 + 날짜 기준 할일 목록
    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByMenteeAndDate(Long menteeId, LocalDate planDate) {
        return todoItemRepository.findByMentee_MenteeIdAndPlanner_PlanDateOrderByCreateTimeAsc(menteeId, planDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 할일 단건 조회 
    @Transactional(readOnly = true)
    public TodoItemResponse getOne(Long menteeId, Long todoItemId) {
        TodoItem todo = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new ResourceNotFoundException("할일을 찾을 수 없습니다. todoItemId=" + todoItemId));
        if (!todo.getMentee().getMenteeId().equals(menteeId)) {
            throw new ResourceNotFoundException("해당 할일은 조회 권한이 없습니다.");
        }
        return toResponse(todo);
    }

    // 멘티가 할일 수정 (고정 할일이면 403) 
    @Transactional
    public TodoItemResponse updateByMentee(Long menteeId, Long todoItemId, TodoItemUpdateRequest request) {
        TodoItem todo = findTodoAndValidateMentee(menteeId, todoItemId);
        if (Boolean.TRUE.equals(todo.getIsFixed())) {
            throw new MentorFixedTodoException();
        }
        applyUpdate(todo, request);
        return toResponse(todoItemRepository.save(todo));
    }

    // 멘토가 할일 수정
    @Transactional
    public TodoItemResponse updateByMentor(Long mentorId, Long menteeId, Long todoItemId, TodoItemUpdateRequest request) {
        TodoItem todo = findTodoAndValidateMentee(menteeId, todoItemId);
        todo = todoItemRepository.save(applyUpdate(todo, request));
        return toResponse(todo);
    }

    // 멘티가 할일 삭제 (고정 할일이면 403)
    @Transactional
    public void deleteByMentee(Long menteeId, Long todoItemId) {
        TodoItem todo = findTodoAndValidateMentee(menteeId, todoItemId);
        if (Boolean.TRUE.equals(todo.getIsFixed())) {
            throw new MentorFixedTodoException();
        }
        todoItemRepository.delete(todo);
    }

    // 멘토가 할일 삭제
    @Transactional
    public void deleteByMentor(Long mentorId, Long menteeId, Long todoItemId) {
        TodoItem todo = findTodoAndValidateMentee(menteeId, todoItemId);
        todoItemRepository.delete(todo);
    }

    private TodoItem findTodoAndValidateMentee(Long menteeId, Long todoItemId) {
        TodoItem todo = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new ResourceNotFoundException("할일을 찾을 수 없습니다. todoItemId=" + todoItemId));
        if (!todo.getMentee().getMenteeId().equals(menteeId)) {
            throw new ResourceNotFoundException("해당 할일은 권한이 없습니다.");
        }
        return todo;
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
