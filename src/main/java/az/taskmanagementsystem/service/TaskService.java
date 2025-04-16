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
import az.taskmanagementsystem.exception.UserNotFoundException;
import az.taskmanagementsystem.mapper.TaskMapper;
import az.taskmanagementsystem.rabbitmq.producer.EmailProducer;
import az.taskmanagementsystem.repository.TaskRepository;
import az.taskmanagementsystem.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    private final UserRepository userRepository;

    private final TaskMapper mapper;

    private final AuthenticationService authenticationService;

    private final UserService userService;

    private final EmailProducer emailProducer;

    @Cacheable(value = "taskList")
    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        System.out.println("fetches from db");
        var authenticatedUser = authenticationService.getLoggedInUser();
        var role = authenticatedUser.getRole();

        if (role.equals(Role.ADMIN)) {
            return repository.findAll().stream()
                    .map(task -> mapper.mapTaskBasedOnRole(task, role))
                    .collect(Collectors.toList());

        } else if (role.equals(Role.MANAGER)) {
            var user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
            return user.getCreatedTasks().stream()
                    .map(task -> mapper.mapTaskBasedOnRole(task, role))
                    .collect(Collectors.toList());

        } else if (role.equals(Role.EMPLOYEE)) {
            var user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
            return user.getAssignedTasks().stream()
                    .map(task -> mapper.mapTaskBasedOnRole(task, role))
                    .collect(Collectors.toList());
        }
        throw new UnauthorizedAccessException();
    }

    @Transactional
    @CacheEvict(value = "taskList", allEntries = true)
    public TaskResponse createTask(TaskCreateRequest request) {
        var user = authenticationService.getLoggedInUser();
        var taskEntity= mapper.dtoToEntity(request);
        if (request.getAssignedUserEmail() != null) {
            var assignedUser = userService.getByEmail(request.getAssignedUserEmail());
            if (!assignedUser.isEnabled()) {
                throw new UserNotFoundException();
            }
            taskEntity.setAssignedUser(assignedUser);
        }
        taskEntity.setCreatedBy(user);
        taskEntity = repository.save(taskEntity);
        emailProducer.sendTaskDeadlineNotification(taskEntity);
        return mapper.entityToDto(taskEntity);
    }

    @Transactional
    @CachePut(value = "tasks", key = "#taskId") // Update cache for the modified task
    @CacheEvict(value = "taskList", allEntries = true)
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        var task = getTaskById(taskId);
        if (!task.getStatus().equals(Status.TODO)) throw new UnsupportedOperationException();
        var user = authenticationService.getLoggedInUser();
        if (!task.getCreatedBy().getId().equals(user.getId())) throw new TaskNotFoundException();
        mapper.updateTask(request, task);
        repository.save(task);
        if (request.getDeadline() != null) {
            emailProducer.sendTaskDeadlineNotification(task);
        }
        return mapper.entityToDto(task);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "tasks", key = "#id"),
            @CacheEvict(value = "taskList", allEntries = true)
    })
    public void deleteTaskById(Long id) {
        var task = getTaskById(id);
        var user = authenticationService.getLoggedInUser();

        switch (user.getRole()) {
            case MANAGER -> {
                if (!task.getCreatedBy().equals(user)) {
                    throw new TaskNotFoundException();
                }
            }
            case EMPLOYEE -> {
                if (!task.getAssignedUser().equals(user)) {
                    throw new TaskNotFoundException();
                }
                if (!task.getStatus().equals(Status.TODO)) {
                    throw new UnsupportedOperationException();
                }
            }
        }
        repository.deleteById(id);
    }

    @Transactional
    @CachePut(value = "tasks", key = "#id")
    @CacheEvict(value = "taskList", allEntries = true)
    public void updateTaskStatus(Long id, Status status) {
        var user = authenticationService.getLoggedInUser();
        user = userService.getByEmail(user.getEmail());
        var task = getTaskById(id);
        if (!user.getAssignedTasks().contains(task)) {
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
        List<TaskResponse> taskResponses = taskPage.getContent().stream()
                .map(taskEntity -> mapper.mapTaskBasedOnRole(taskEntity, user.getRole()))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                taskResponses,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isLast()
        );
    }

    public Task getTaskById(Long id) {
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
