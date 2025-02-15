package az.taskmanagementsystem.controller;

import az.taskmanagementsystem.dto.TaskRequest;
import az.taskmanagementsystem.dto.TaskResponse;
import az.taskmanagementsystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(){
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<Void> addTask(@RequestBody TaskRequest request){
        service.add(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateTask(@RequestParam Long id, @RequestBody TaskRequest request){
        service.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
