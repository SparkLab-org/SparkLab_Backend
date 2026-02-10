package com.sparkLab.study.planner.service;

import com.sparkLab.study.activity.service.NotificationService;
import com.sparkLab.study.planner.entity.DailyPlan;
import com.sparkLab.study.planner.exception.PlannerFixedTodoException;
import com.sparkLab.study.planner.exception.PlannerResourceNotFoundException;
import com.sparkLab.study.planner.repository.DailyPlanRepository;
import com.sparkLab.study.planner.repository.TodoItemRepository;
import com.sparkLab.study.planner.dto.todo.DateTodosGroup;
import com.sparkLab.study.planner.dto.todo.MenteeTodosResponse;
import com.sparkLab.study.planner.dto.todo.TodoItemCreateRequest;
import com.sparkLab.study.planner.dto.todo.TodoItemResponse;
import com.sparkLab.study.planner.dto.todo.TodoItemUpdateRequest;
import com.sparkLab.study.planner.entity.TodoItem;
import com.sparkLab.study.task.entity.Assignment;
import com.sparkLab.study.task.repository.AssignmentRepository;
import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final NotificationService notificationService;
    private final AssignmentRepository assignmentRepository;
    private final MenteeRepository menteeRepository;

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
        return todoItemRepository.findByDailyPlan_dailyPlanIdOrderByCreateTimeAsc(plannerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 날짜 기준 할일 목록
    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByPlanDate(LocalDate planDate) {
        return todoItemRepository.findByDailyPlan_PlanDateOrderByCreateTimeAsc(planDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 멘토: 할일 목록을 멘티별로 묶어서 조회 (날짜별, 멘티별 필터)
     * - planDate: 해당 날짜의 할일만 (없으면 전체 기간)
     * - menteeId: 해당 멘티만 (없으면 전체 멘티)
     */
    @Transactional(readOnly = true)
    public List<MenteeTodosResponse> listByMentorGroupedByMentee(Long mentorId, LocalDate planDate, Long menteeId) {
        List<Mentee> mentees;
        if (menteeId != null) {
            Mentee mentee = menteeRepository.findById(menteeId)
                    .orElseThrow(() -> new PlannerResourceNotFoundException("멘티를 찾을 수 없습니다. menteeId=" + menteeId));
            if (mentee.getMentor() == null || !mentee.getMentor().getMentorId().equals(mentorId)) {
                throw new PlannerResourceNotFoundException("해당 멘티는 해당 멘토 소속이 아닙니다.");
            }
            mentees = List.of(mentee);
        } else {
            mentees = menteeRepository.findByMentor_MentorId(mentorId);
        }

        return mentees.stream()
                .map(mentee -> {
                    List<TodoItem> todos;
                    if (planDate != null) {
                        todos = todoItemRepository.findByDailyPlan_Mentee_MenteeIdAndDailyPlan_PlanDateOrderByCreateTimeAsc(
                                mentee.getMenteeId(), planDate);
                    } else {
                        todos = todoItemRepository.findByMentee_MenteeIdOrderByTargetDateDescCreateTimeAsc(mentee.getMenteeId());
                    }
                    // 멘티당 날짜별로 그룹화 (날짜 내림차순)
                    List<DateTodosGroup> todosByDate = todos.stream()
                            .collect(Collectors.groupingBy(
                                    t -> t.getDailyPlan() != null ? t.getDailyPlan().getPlanDate() : t.getTargetDate(),
                                    Collectors.mapping(this::toResponse, Collectors.toList())))
                            .entrySet().stream()
                            .map(e -> DateTodosGroup.builder()
                                    .planDate(e.getKey())
                                    .todos(e.getValue())
                                    .build())
                            .sorted(Comparator.comparing(DateTodosGroup::getPlanDate).reversed())
                            .collect(Collectors.toList());
                    return MenteeTodosResponse.builder()
                            .menteeId(mentee.getMenteeId())
                            .accountId(mentee.getAccount() != null ? mentee.getAccount().getAccountId() : null)
                            .activeLevel(mentee.getActiveLevel())
                            .todosByDate(todosByDate)
                            .build();
                })
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
            throw new PlannerFixedTodoException();
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
            throw new PlannerFixedTodoException();
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
                .orElseThrow(() -> new PlannerResourceNotFoundException("할일을 찾을 수 없습니다. todoItemId=" + todoItemId));
    }

    private TodoItemResponse createTodo(TodoItemCreateRequest request, boolean isFixed) {
        DailyPlan dailyPlan = dailyPlanRepository.findById(request.getPlannerId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("플래너를 찾을 수 없습니다. plannerId=" + request.getPlannerId()));
        if (dailyPlan.getMentee() == null) {
            throw new PlannerResourceNotFoundException("플래너에 연결된 멘티가 없습니다. plannerId=" + request.getPlannerId());
        }
        TodoItem todo = TodoItem.builder()
                .mentee(dailyPlan.getMentee())
                .mentor(isFixed ? dailyPlan.getMentee().getMentor() : null)
                .dailyPlan(dailyPlan)
                .targetDate(request.getTargetDate() != null ? request.getTargetDate() : dailyPlan.getPlanDate())
                .title(request.getTitle())
                .subject(request.getSubject())
                .type(request.getType())
                .isFixed(isFixed)
                .status("TODO")
                .plannedMinutes(request.getPlannedMinutes())
                .build();
        todo = todoItemRepository.save(todo);
        notificationService.notifyNewTodo(todo);

        // 타입이 ASSIGNMENT인 Todo는 Assignment를 함께 생성
        if ("ASSIGNMENT".equalsIgnoreCase(todo.getType())) {
            Assignment assignment = Assignment.builder()
                    .todoItem(todo)
                    .mentor(todo.getMentor())
                    .materialTitle(todo.getTitle())
                    .build();
            assignmentRepository.save(assignment);
        }

        return toResponse(todo);
    }

    private TodoItem applyUpdate(TodoItem todo, TodoItemUpdateRequest request) {
        if (request.getTitle() != null) todo.setTitle(request.getTitle());
        if (request.getTargetDate() != null) todo.setTargetDate(request.getTargetDate());
        if (request.getSubject() != null) todo.setSubject(request.getSubject());
        if (request.getType() != null) todo.setType(request.getType());
        if (request.getStatus() != null) todo.setStatus(request.getStatus());
        if (request.getPlannedMinutes() != null) todo.setPlannedMinutes(request.getPlannedMinutes());
        if (request.getActualMinutes() != null) todo.setActualMinutes(request.getActualMinutes());
        if (request.getActualSeconds() != null) todo.setActualSeconds(request.getActualSeconds());
        if (request.getCompletedAt() != null) todo.setCompletedAt(request.getCompletedAt());
        return todo;
    }

    private TodoItemResponse toResponse(TodoItem todo) {
        Long assignmentId = null;
        if (todo.getAssignments() != null && !todo.getAssignments().isEmpty()) {
            assignmentId = todo.getAssignments().get(0).getAssignmentId();
        }

        return TodoItemResponse.builder()
                .todoItemId(todo.getTodoItemId())
                .plannerId(todo.getDailyPlan().getDailyPlanId())
                .assignmentId(assignmentId)
                .targetDate(todo.getTargetDate())
                .title(todo.getTitle())
                .subject(todo.getSubject())
                .type(todo.getType())
                .isFixed(todo.getIsFixed())
                .status(todo.getStatus())
                .plannedMinutes(todo.getPlannedMinutes())
                .actualMinutes(todo.getActualMinutes())
                .actualSeconds(todo.getActualSeconds())
                .completedAt(todo.getCompletedAt())
                .createTime(todo.getCreateTime())
                .updateTime(todo.getUpdateTime())
                .build();
    }
}
