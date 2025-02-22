package az.taskmanagementsystem.controller;

import az.taskmanagementsystem.dto.PaginatedResponse;
import az.taskmanagementsystem.dto.TaskRequest;
import az.taskmanagementsystem.dto.TaskResponse;
import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import az.taskmanagementsystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Void> addTask(@RequestBody TaskRequest request) {
        service.add(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateTask(@RequestParam Long id, @RequestBody TaskRequest request) {
        service.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign-task-to")
    public ResponseEntity<Void> assignTaskToEmployee(@RequestParam String email, @RequestParam Long taskId) {
        service.assignTask(email, taskId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long taskId, @RequestParam Status status) {
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
