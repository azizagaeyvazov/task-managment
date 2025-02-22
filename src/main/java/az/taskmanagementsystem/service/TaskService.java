package az.taskmanagementsystem.service;

import az.taskmanagementsystem.dto.PaginatedResponse;
import az.taskmanagementsystem.dto.TaskCreateRequest;
import az.taskmanagementsystem.dto.TaskResponse;
import az.taskmanagementsystem.dto.TaskUpdateRequest;
import az.taskmanagementsystem.entity.QTask;
import az.taskmanagementsystem.entity.Task;
import az.taskmanagementsystem.entity.User;
import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Role;
import az.taskmanagementsystem.enums.Status;
import az.taskmanagementsystem.exception.TaskNotFoundException;
import az.taskmanagementsystem.exception.UnauthorizedAccessException;
import az.taskmanagementsystem.mapper.TaskMapper;
import az.taskmanagementsystem.repository.TaskRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    private final TaskMapper mapper;

    private final AuthenticationService authenticationService;

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        var user = authenticationService.getLoggedInUser();
        var role = user.getRole();

        if (role.equals(Role.ADMIN)) {
            return repository.findAll().stream()
                    .map(task -> mapper.mapTaskBasedOnRole(task, role))
                    .collect(Collectors.toList());
        } else if (role.equals(Role.MANAGER)) {
            return user.getCreatedTasks().stream()
                    .map(task -> mapper.mapTaskBasedOnRole(task, role))
                    .collect(Collectors.toList());
        } else if (role.equals(Role.EMPLOYEE)) {
            return user.getAssignedTasks().stream()
                    .map(task -> mapper.mapTaskBasedOnRole(task, role))
                    .collect(Collectors.toList());
        }
        throw new UnauthorizedAccessException();
    }

    @Transactional
    public void createTask(TaskCreateRequest request) {
        var task = mapper.dtoToEntity(request);
        var manager = authenticationService.getLoggedInUser();
        if (request.getAssignedUserEmail() != null) {
            var assignedUser = userService.getByEmail(request.getAssignedUserEmail());
            task.setAssignedUser(assignedUser);
        }
        task.setCreatedBy(manager);
        repository.save(task);
    }

    @Transactional
    public void updateTask(Long id, TaskUpdateRequest request) {
        if (authenticationService.getLoggedInUser().getRole().equals(Role.EMPLOYEE))
            throw new UnauthorizedAccessException();
        var task = getTaskById(id);
        if (request.getAssignedUserEmail() != null) {
            var assignedUser = userService.getByEmail(request.getAssignedUserEmail());
            task.setAssignedUser(assignedUser);
        }
        mapper.updateTask(request, task);
        repository.save(task);
    }

    @Transactional
    public void deleteTaskById(Long id) {
        var task = getTaskById(id);
        var user = authenticationService.getLoggedInUser();
        var role = user.getRole();

        if (role.equals(Role.ADMIN)) repository.deleteById(id);
        if (role.equals(Role.MANAGER) && task.getCreatedBy().equals(user)) repository.deleteById(id);
        if (role.equals(Role.EMPLOYEE) && task.getAssignedUser().equals(user) && task.getStatus().equals(Status.PENDING))
            repository.deleteById(id);
        throw new UnauthorizedAccessException();
    }

    @Transactional
    public void assignTask(String email, Long taskId) {
        var task = getTaskById(taskId);
        var employee = userService.getByEmail(email);
        task.setAssignedUser(employee);
        repository.save(task);
    }

    @Transactional
    public void updateTaskStatus(Long taskId, Status status) {
        var employee = authenticationService.getLoggedInUser();
        var task = getTaskById(taskId);
        if (!employee.getAssignedTasks().contains(task)) {
            throw new TaskNotFoundException();
        }
        task.setStatus(status);
        repository.save(task);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> getFilteredTasks(String title, Status status, Priority priority, String tags, Pageable pageable) {

        var user = authenticationService.getLoggedInUser();
        QTask task = QTask.task;
        BooleanBuilder predicate = new BooleanBuilder();

        if (title != null && !title.isEmpty()) predicate.and(task.title.containsIgnoreCase(title));
        if (status != null) predicate.and(task.status.eq(status));
        if (priority != null) predicate.and(task.priority.eq(priority));
        if (tags != null && !tags.isEmpty()) predicate.and(task.tags.containsIgnoreCase(tags));

        applyRoleBasedFilter(predicate, user);

        Page<Task> taskPage = repository.findAll(predicate, pageable);

        List<TaskResponse> tasksResponse = taskPage.getContent().stream()
                .map(taskEntity -> mapper.mapTaskBasedOnRole(taskEntity, user.getRole()))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                tasksResponse,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isLast()
        );
    }

    private Task getTaskById(Long id) {
        return repository.findById(id).orElseThrow(TaskNotFoundException::new);
    }

    private void applyRoleBasedFilter(BooleanBuilder predicate, User user) {
        if (user.getRole().equals(Role.MANAGER)) {
            predicate.and(QTask.task.createdBy.eq(user));
        } else if (user.getRole().equals(Role.EMPLOYEE)) {
            predicate.and(QTask.task.assignedUser.eq(user));
        } else if (!user.getRole().equals(Role.ADMIN)) {
            throw new UnauthorizedAccessException();
        }
    }
}
