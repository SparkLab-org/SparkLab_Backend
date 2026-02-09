package com.sparkLab.study.service;

import com.sparkLab.study.dto.assignment.AssignmentSubmissionResponse;
import com.sparkLab.study.dto.todo.TodoAssignmentDetailResponse;
import com.sparkLab.study.dto.todo.TodoItemCreateRequest;
import com.sparkLab.study.dto.todo.TodoItemResponse;
import com.sparkLab.study.dto.todo.TodoItemUpdateRequest;
import com.sparkLab.study.entity.Assignment;
import com.sparkLab.study.entity.AssignmentSubmission;
import com.sparkLab.study.entity.Planner;
import com.sparkLab.study.entity.TodoItem;
import com.sparkLab.study.exception.PlannerFixedTodoException;
import com.sparkLab.study.exception.PlannerResourceNotFoundException;
import com.sparkLab.study.repository.AssignmentRepository;
import com.sparkLab.study.repository.PlannerRepository;
import com.sparkLab.study.repository.TodoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;
    private final AssignmentRepository assignmentRepository;
    private final PlannerRepository plannerRepository;
    private final NotificationService notificationService;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    // 할일 생성
    @Transactional
    public TodoItemResponse create(TodoItemCreateRequest request) {
        return createTodo(request, false, null, null);
    }

    // 고정 할일 생성
    @Transactional
    public TodoItemResponse createFixed(TodoItemCreateRequest request) {
        return createTodo(request, true, null, null);
    }

    // 할일 생성 (학습지 포함)
    @Transactional
    public TodoItemResponse createWithMaterial(TodoItemCreateRequest request, MultipartFile materialFile) {
        return createTodo(request, false, materialFile, null);
    }

    // 고정 할일 생성 (학습지 포함)
    @Transactional
    public TodoItemResponse createFixedWithMaterial(TodoItemCreateRequest request, MultipartFile materialFile) {
        return createTodo(request, true, materialFile, null);
    }

    @Transactional
    public TodoItemResponse createAssignment(TodoItemCreateRequest request, MultipartFile materialFile) {
        requireSubject(request);
        return createTodo(request, false, materialFile, "ASSIGNMENT");
    }

    @Transactional
    public TodoItemResponse createFixedAssignment(TodoItemCreateRequest request, MultipartFile materialFile) {
        requireSubject(request);
        return createTodo(request, true, materialFile, "ASSIGNMENT");
    }

    @Transactional
    public TodoItemResponse createStudy(TodoItemCreateRequest request, MultipartFile materialFile) {
        requireSubject(request);
        return createTodo(request, false, materialFile, "STUDY");
    }

    @Transactional
    public TodoItemResponse createFixedStudy(TodoItemCreateRequest request, MultipartFile materialFile) {
        requireSubject(request);
        return createTodo(request, true, materialFile, "STUDY");
    }

    // 플래너 기준 할일 목록 
    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByPlannerId(Long plannerId) {
        return todoItemRepository.findByPlanner_PlannerIdOrderByCreateTimeAsc(plannerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByPlannerIdAndType(Long plannerId, String type, com.sparkLab.study.constant.Subject subject) {
        return todoItemRepository.findByPlanner_PlannerIdAndTypeOrderByCreateTimeAsc(plannerId, type).stream()
                .filter(todo -> subject == null || subject == todo.getSubject())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 날짜 기준 할일 목록 (전체 타입, 과목 필터 선택)
    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByPlanDate(LocalDate planDate) {
        return listByPlanDate(planDate, null);
    }

    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByPlanDate(LocalDate planDate, com.sparkLab.study.constant.Subject subject) {
        return todoItemRepository.findByPlanner_PlanDateOrderByCreateTimeAsc(planDate).stream()
                .filter(todo -> subject == null || subject == todo.getSubject())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TodoItemResponse> listByPlanDateAndType(LocalDate planDate, String type, com.sparkLab.study.constant.Subject subject) {
        return todoItemRepository.findByPlanner_PlanDateAndTypeOrderByCreateTimeAsc(planDate, type).stream()
                .filter(todo -> subject == null || subject == todo.getSubject())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 할일 단건 조회 
    @Transactional(readOnly = true)
    public TodoItemResponse getOne(Long todoItemId) {
        TodoItem todo = findTodo(todoItemId);
        return toResponse(todo);
    }

    @Transactional(readOnly = true)
    public TodoItemResponse getOneByType(Long todoItemId, String type) {
        TodoItem todo = findTodo(todoItemId);
        if (!type.equals(todo.getType())) {
            throw new PlannerResourceNotFoundException("할일 타입이 일치하지 않습니다. todoItemId=" + todoItemId);
        }
        return toResponse(todo);
    }

    /** 과제 상세 조회: 할일 내용 + 첨부 파일 + 멘티 제출 목록 */
    @Transactional(readOnly = true)
    public TodoAssignmentDetailResponse getAssignmentDetail(Long todoItemId) {
        TodoItem todo = findTodo(todoItemId);
        if (!"ASSIGNMENT".equals(todo.getType())) {
            throw new PlannerResourceNotFoundException("과제가 아닌 할일입니다. todoItemId=" + todoItemId);
        }
        List<Assignment> assignments = assignmentRepository.findByTodoItem_TodoItemIdOrderByAssignmentIdAsc(todoItemId);
        Long assignmentId = null;
        List<AssignmentSubmissionResponse> submissions = new ArrayList<>();
        if (!assignments.isEmpty()) {
            Assignment assignment = assignments.get(0);
            assignmentId = assignment.getAssignmentId();
            if (assignment.getSubmissions() != null) {
                submissions = assignment.getSubmissions().stream()
                        .map(this::toSubmissionResponse)
                        .collect(Collectors.toList());
            }
        }
        return TodoAssignmentDetailResponse.builder()
                .todoItemId(todo.getTodoItemId())
                .plannerId(todo.getPlanner().getPlannerId())
                .assignmentId(assignmentId)
                .targetDate(todo.getTargetDate())
                .title(todo.getTitle())
                .subject(todo.getSubject())
                .type(todo.getType())
                .goal(todo.getGoal())
                .materialType(todo.getMaterialType())
                .materialUrl(todo.getMaterialUrl())
                .isFixed(todo.getIsFixed())
                .status(todo.getStatus())
                .plannedMinutes(todo.getPlannedMinutes())
                .actualMinutes(todo.getActualMinutes())
                .actualSeconds(todo.getActualSeconds())
                .completedAt(todo.getCompletedAt())
                .createTime(todo.getCreateTime())
                .updateTime(todo.getUpdateTime())
                .submissions(submissions)
                .build();
    }

    private AssignmentSubmissionResponse toSubmissionResponse(AssignmentSubmission s) {
        return AssignmentSubmissionResponse.builder()
                .submissionId(s.getSubmissionId())
                .assignmentId(s.getAssignment().getAssignmentId())
                .menteeId(s.getMentee().getMenteeId())
                .imageUrl(s.getImageUrl())
                .comment(s.getComment())
                .status(s.getStatus())
                .createTime(s.getCreateTime())
                .build();
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

    @Transactional
    public TodoItemResponse updateByType(Long todoItemId, String type, TodoItemUpdateRequest request, boolean allowFixed) {
        TodoItem todo = findTodo(todoItemId);
        if (!type.equals(todo.getType())) {
            throw new PlannerResourceNotFoundException("할일 타입이 일치하지 않습니다. todoItemId=" + todoItemId);
        }
        if (!allowFixed && Boolean.TRUE.equals(todo.getIsFixed())) {
            throw new PlannerFixedTodoException();
        }
        request.setType(type);
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

    @Transactional
    public void deleteByType(Long todoItemId, String type, boolean allowFixed) {
        TodoItem todo = findTodo(todoItemId);
        if (!type.equals(todo.getType())) {
            throw new PlannerResourceNotFoundException("할일 타입이 일치하지 않습니다. todoItemId=" + todoItemId);
        }
        if (!allowFixed && Boolean.TRUE.equals(todo.getIsFixed())) {
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

    private TodoItemResponse createTodo(TodoItemCreateRequest request, boolean isFixed, MultipartFile materialFile, String forcedType) {
        Planner planner = plannerRepository.findById(request.getPlannerId())
                .orElseThrow(() -> new PlannerResourceNotFoundException("플래너를 찾을 수 없습니다. plannerId=" + request.getPlannerId()));
        if (planner.getMentee() == null) {
            throw new PlannerResourceNotFoundException("플래너에 연결된 멘티가 없습니다. plannerId=" + request.getPlannerId());
        }
        TodoItem todo = TodoItem.builder()
                .mentee(planner.getMentee())
                .mentor(isFixed ? planner.getMentee().getMentorId() : null)
                .planner(planner)
                .targetDate(request.getTargetDate() != null ? request.getTargetDate() : planner.getPlanDate())
                .title(request.getTitle())
                .subject(request.getSubject())
                .type(forcedType != null ? forcedType : request.getType())
                .goal(request.getGoal())
                .materialType(resolveMaterialType(request, materialFile))
                .materialUrl(resolveMaterialUrl(request, materialFile))
                .isFixed(isFixed)
                .status("TODO")
                .plannedMinutes(request.getPlannedMinutes())
                .build();
        todo = todoItemRepository.save(todo);
        if (materialFile != null && !materialFile.isEmpty()) {
            String materialUrl = storeMaterialFile(todo.getTodoItemId(), materialFile);
            todo.setMaterialUrl(materialUrl);
            todo.setMaterialType("PDF");
            todo = todoItemRepository.save(todo);
        }
        if ("ASSIGNMENT".equals(todo.getType())) {
            Assignment assignment = Assignment.builder()
                    .todoItem(todo)
                    .mentor(todo.getMentor())
                    .materialType(todo.getMaterialType())
                    .materialTitle(todo.getTitle())
                    .materialFileUrl(todo.getMaterialUrl())
                    .build();
            assignmentRepository.save(assignment);
        }
        notificationService.notifyNewTodo(todo);
        return toResponse(todo);
    }

    private TodoItem applyUpdate(TodoItem todo, TodoItemUpdateRequest request) {
        if (request.getTitle() != null) todo.setTitle(request.getTitle());
        if (request.getTargetDate() != null) todo.setTargetDate(request.getTargetDate());
        if (request.getSubject() != null) todo.setSubject(request.getSubject());
        if (request.getType() != null) todo.setType(request.getType());
        if (request.getGoal() != null) todo.setGoal(request.getGoal());
        if (request.getMaterialType() != null) todo.setMaterialType(request.getMaterialType());
        if (request.getMaterialUrl() != null) todo.setMaterialUrl(request.getMaterialUrl());
        if (request.getStatus() != null) todo.setStatus(request.getStatus());
        if (request.getPlannedMinutes() != null) todo.setPlannedMinutes(request.getPlannedMinutes());
        if (request.getActualMinutes() != null) todo.setActualMinutes(request.getActualMinutes());
        if (request.getActualSeconds() != null) todo.setActualSeconds(request.getActualSeconds());
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
                .goal(todo.getGoal())
                .materialType(todo.getMaterialType())
                .materialUrl(todo.getMaterialUrl())
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

    private void requireSubject(TodoItemCreateRequest request) {
        if (request.getSubject() == null) {
            throw new IllegalArgumentException("과목은 필수입니다.");
        }
    }

    private String resolveMaterialType(TodoItemCreateRequest request, MultipartFile materialFile) {
        if (materialFile != null && !materialFile.isEmpty()) {
            return "PDF";
        }
        if (request.getMaterialType() != null) {
            return request.getMaterialType();
        }
        return request.getMaterialUrl() != null ? "COLUMN" : null;
    }

    private String resolveMaterialUrl(TodoItemCreateRequest request, MultipartFile materialFile) {
        if (materialFile != null && !materialFile.isEmpty()) {
            return null;
        }
        return request.getMaterialUrl();
    }

    private String storeMaterialFile(Long todoItemId, MultipartFile file) {
        validateMaterialFile(file);
        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        Path uploadPath = Paths.get(uploadDir, "todos", String.valueOf(todoItemId));
        createDirectories(uploadPath);
        Path targetPath = uploadPath.resolve(filename).normalize();
        try {
            Files.copy(file.getInputStream(), targetPath);
        } catch (IOException e) {
            throw new IllegalArgumentException("학습지 파일 저장에 실패했습니다.");
        }
        return "/uploads/todos/" + todoItemId + "/" + filename;
    }

    private void validateMaterialFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("학습지 파일은 필수입니다.");
        }
        String contentType = file.getContentType();
        String extension = getExtension(file.getOriginalFilename());
        if ((contentType != null && !"application/pdf".equals(contentType))
                && !"pdf".equals(extension)) {
            throw new IllegalArgumentException("학습지 파일은 PDF만 업로드할 수 있습니다.");
        }
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    private void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("업로드 경로를 생성할 수 없습니다.");
        }
    }
}
