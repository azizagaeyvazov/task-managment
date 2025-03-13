package az.taskmanagementsystem.controller;

import az.taskmanagementsystem.dto.PaginatedResponse;
import az.taskmanagementsystem.dto.TaskCreateRequest;
import az.taskmanagementsystem.dto.TaskResponse;
import az.taskmanagementsystem.dto.TaskUpdateRequest;
import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import az.taskmanagementsystem.service.TaskService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<Void> addTask(@RequestBody TaskCreateRequest request) {
        service.createTask(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateTask(@RequestParam Long id, @RequestBody TaskUpdateRequest request) {
        service.updateTask(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        service.deleteTaskById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign-task")
    public ResponseEntity<Void> assignTaskToEmployee(@RequestParam String email, @RequestParam Long taskId) {
        service.assignTask(email, taskId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long taskId,
                                                 @RequestParam
                                                 @NotNull(message = "Status is required")
                                                 @Pattern(regexp = "TODO|PENDING|COMPLETED",
                                                         message = "Status must be either TODO, PENDING, or COMPLETED")
                                                 Status status) {
        service.updateTaskStatus(taskId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<PaginatedResponse<TaskResponse>> getTasksByStatus(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String tags,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getFilteredTasks(title, status, priority, tags, pageable));
    }
}
