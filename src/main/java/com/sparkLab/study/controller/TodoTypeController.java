package com.sparkLab.study.controller;

import com.sparkLab.study.constant.Subject;
import com.sparkLab.study.dto.todo.TodoItemCreateRequest;
import com.sparkLab.study.dto.todo.TodoItemResponse;
import com.sparkLab.study.dto.todo.TodoItemUpdateRequest;
import com.sparkLab.study.service.TodoItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("todos")
@RequiredArgsConstructor
public class TodoTypeController {

    private final TodoItemService todoItemService;

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/assignments")
    public List<TodoItemResponse> listAssignments(
            @RequestParam(required = false) Long plannerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate,
            @RequestParam(required = false) Subject subject) {
        if (plannerId != null) {
            return todoItemService.listByPlannerIdAndType(plannerId, "ASSIGNMENT", subject);
        }
        if (planDate != null) {
            return todoItemService.listByPlanDateAndType(planDate, "ASSIGNMENT", subject);
        }
        throw new IllegalArgumentException("plannerId 또는 planDate 중 하나는 필수입니다.");
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/assignments/date/{planDate}")
    public List<TodoItemResponse> listAssignmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate,
            @RequestParam(required = false) Subject subject) {
        return todoItemService.listByPlanDateAndType(planDate, "ASSIGNMENT", subject);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/studies")
    public List<TodoItemResponse> listStudies(
            @RequestParam(required = false) Long plannerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate,
            @RequestParam(required = false) Subject subject) {
        if (plannerId != null) {
            return todoItemService.listByPlannerIdAndType(plannerId, "STUDY", subject);
        }
        if (planDate != null) {
            return todoItemService.listByPlanDateAndType(planDate, "STUDY", subject);
        }
        throw new IllegalArgumentException("plannerId 또는 planDate 중 하나는 필수입니다.");
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/studies/date/{planDate}")
    public List<TodoItemResponse> listStudiesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate,
            @RequestParam(required = false) Subject subject) {
        return todoItemService.listByPlanDateAndType(planDate, "STUDY", subject);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/assignments/{todoItemId}")
    public TodoItemResponse getAssignment(@PathVariable Long todoItemId) {
        return todoItemService.getOneByType(todoItemId, "ASSIGNMENT");
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @GetMapping("/studies/{todoItemId}")
    public TodoItemResponse getStudy(@PathVariable Long todoItemId) {
        return todoItemService.getOneByType(todoItemId, "STUDY");
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping("/assignments")
    public ResponseEntity<TodoItemResponse> createAssignment(
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.createAssignment(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping(value = "/assignments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TodoItemResponse> createAssignmentWithMaterial(
            @ModelAttribute @Valid TodoItemCreateRequest request,
            @RequestParam(value = "materialFile", required = false) MultipartFile materialFile) {
        TodoItemResponse created = todoItemService.createAssignment(request, materialFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping("/studies")
    public ResponseEntity<TodoItemResponse> createStudy(
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.createStudy(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping(value = "/studies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TodoItemResponse> createStudyWithMaterial(
            @ModelAttribute @Valid TodoItemCreateRequest request,
            @RequestParam(value = "materialFile", required = false) MultipartFile materialFile) {
        TodoItemResponse created = todoItemService.createStudy(request, materialFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping("/assignments/fixed")
    public ResponseEntity<TodoItemResponse> createFixedAssignment(
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.createFixedAssignment(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping(value = "/assignments/fixed", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TodoItemResponse> createFixedAssignmentWithMaterial(
            @ModelAttribute @Valid TodoItemCreateRequest request,
            @RequestParam(value = "materialFile", required = false) MultipartFile materialFile) {
        TodoItemResponse created = todoItemService.createFixedAssignment(request, materialFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping("/studies/fixed")
    public ResponseEntity<TodoItemResponse> createFixedStudy(
            @RequestBody @Valid TodoItemCreateRequest request) {
        TodoItemResponse created = todoItemService.createFixedStudy(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PostMapping(value = "/studies/fixed", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TodoItemResponse> createFixedStudyWithMaterial(
            @ModelAttribute @Valid TodoItemCreateRequest request,
            @RequestParam(value = "materialFile", required = false) MultipartFile materialFile) {
        TodoItemResponse created = todoItemService.createFixedStudy(request, materialFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PutMapping("/assignments/{todoItemId}")
    public TodoItemResponse updateAssignment(
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.updateByType(todoItemId, "ASSIGNMENT", request, false);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PutMapping("/assignments/fixed/{todoItemId}")
    public TodoItemResponse updateFixedAssignment(
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.updateByType(todoItemId, "ASSIGNMENT", request, true);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PutMapping("/studies/{todoItemId}")
    public TodoItemResponse updateStudy(
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.updateByType(todoItemId, "STUDY", request, false);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @PutMapping("/studies/fixed/{todoItemId}")
    public TodoItemResponse updateFixedStudy(
            @PathVariable Long todoItemId,
            @RequestBody TodoItemUpdateRequest request) {
        return todoItemService.updateByType(todoItemId, "STUDY", request, true);
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @DeleteMapping("/assignments/{todoItemId}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long todoItemId) {
        todoItemService.deleteByType(todoItemId, "ASSIGNMENT", false);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @DeleteMapping("/assignments/fixed/{todoItemId}")
    public ResponseEntity<Void> deleteFixedAssignment(@PathVariable Long todoItemId) {
        todoItemService.deleteByType(todoItemId, "ASSIGNMENT", true);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @DeleteMapping("/studies/{todoItemId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long todoItemId) {
        todoItemService.deleteByType(todoItemId, "STUDY", false);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('MENTOR','MENTEE')")
    @DeleteMapping("/studies/fixed/{todoItemId}")
    public ResponseEntity<Void> deleteFixedStudy(@PathVariable Long todoItemId) {
        todoItemService.deleteByType(todoItemId, "STUDY", true);
        return ResponseEntity.noContent().build();
    }
}
