package az.taskmanagementsystem.service;

import az.taskmanagementsystem.dto.PaginatedResponse;
import az.taskmanagementsystem.dto.TaskRequest;
import az.taskmanagementsystem.dto.TaskResponse;
import az.taskmanagementsystem.entity.QTask;
import az.taskmanagementsystem.entity.Task;
import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import az.taskmanagementsystem.mapper.TaskMapper;
import az.taskmanagementsystem.repository.TaskRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    private final TaskMapper mapper;

    private final AuthenticationService authenticationService;

    private final UserService userService;

    private final TaskRepository taskRepository;

    public List<TaskResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    public void add(TaskRequest request) {
        var task = mapper.dtoToEntity(request);
        var manager = authenticationService.getLoggedInUser();
        task.setCreatedBy(manager);
        repository.save(task);
    }

    public void update(Long id, TaskRequest request) {
        var task = getTaskById(id);
        mapper.updateTask(task, request);
        repository.save(task);
    }

    public void deleteById(Long id) {
        if (taskRepository.findById(id).isPresent()) {
            var task = taskRepository.findById(id).get();
            var employee = task.getAssignedUser();
            employee.getAssignedTasks().remove(task);
        }
        repository.deleteById(id);
    }

    private Task getTaskById(Long id) {
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void assignTask(String email, Long taskId) {
        var task = getTaskById(taskId);
        var employee = userService.getByEmail(email);
        task.setAssignedUser(employee);
        repository.save(task);
    }

    public void updateTaskStatus(Long taskId, Status status) {
        var employee = authenticationService.getLoggedInUser();
        var task = taskRepository.findById(taskId).orElseThrow(RuntimeException::new);
        if (!task.getAssignedUser().getEmail().equals(employee.getEmail())) {
            throw new RuntimeException();
        }
        task.setStatus(status);
        taskRepository.save(task);
    }

    public PaginatedResponse<TaskResponse> getFilteredTasks(String title, Status status, Priority priority, String tags, Pageable pageable) {

        QTask task = QTask.task;
        BooleanBuilder predicate = new BooleanBuilder();

        if (title != null && !title.isEmpty()) predicate.and(task.title.containsIgnoreCase(title));

        if (status != null) predicate.and(task.status.eq(status));

        if (priority != null) predicate.and(task.priority.eq(priority));

        if (tags != null && !tags.isEmpty()) predicate.and(task.tags.containsIgnoreCase(tags)); // Assuming simple tag search

        var taskPage = taskRepository
                .findAll(predicate, pageable)
                .map(mapper::entityToDto);
        return new PaginatedResponse<>(
                taskPage.getContent(),
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isLast()
        );
    }
}
