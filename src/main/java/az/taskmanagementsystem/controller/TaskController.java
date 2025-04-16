package az.taskmanagementsystem.controller;

import az.taskmanagementsystem.dto.PaginatedResponse;
import az.taskmanagementsystem.dto.TaskCreateRequest;
import az.taskmanagementsystem.dto.TaskResponse;
import az.taskmanagementsystem.dto.TaskUpdateRequest;
import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import az.taskmanagementsystem.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks() {
        return ResponseEntity.ok(taskService.getAll());
    }

    @PostMapping
    public ResponseEntity<TaskResponse> addTask(@Valid @RequestBody TaskCreateRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @PatchMapping
    public ResponseEntity<TaskResponse> updateTask(@RequestParam Long taskId, @Valid @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<String> updateTaskStatus(@PathVariable Long taskId,
                                                   @RequestParam
                                                   @NotNull(message = "Status is required")
                                                   Status status) {
        taskService.updateTaskStatus(taskId, status);
        return ResponseEntity.ok("Status of the task has been updated to " + status + " successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.ok("The task with id " + id + " has been updated successfully.");
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<TaskResponse>> searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String tags,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.getFilteredTasks(title, status, priority, tags, pageable));
    }
}
