package az.taskmanagementsystem.service;

import az.taskmanagementsystem.dto.TaskRequest;
import az.taskmanagementsystem.dto.TaskResponse;
import az.taskmanagementsystem.entity.Task;
import az.taskmanagementsystem.mapper.TaskMapper;
import az.taskmanagementsystem.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    private final TaskMapper mapper;

    public List<TaskResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    public void add(TaskRequest request) {
        var task = mapper.dtoToEntity(request);
        repository.save(task);
    }

    public void update(Long id, TaskRequest request) {
        var task = getTaskById(id);
        mapper.updateTask(task, request);
        repository.save(task);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private Task getTaskById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
