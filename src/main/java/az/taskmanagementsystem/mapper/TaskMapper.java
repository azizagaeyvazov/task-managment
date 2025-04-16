package az.taskmanagementsystem.mapper;

import az.taskmanagementsystem.dto.TaskCreateRequest;
import az.taskmanagementsystem.dto.TaskResponse;
import az.taskmanagementsystem.dto.TaskUpdateRequest;
import az.taskmanagementsystem.entity.Task;
import az.taskmanagementsystem.enums.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "assignedUser", expression = "java(task.getAssignedUser().getFullName())")
    @Mapping(target = "createdBy", expression = "java(task.getCreatedBy().getFullName())")
    TaskResponse entityToDto(Task task);

    Task dtoToEntity(TaskCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTask(TaskUpdateRequest request, @MappingTarget Task task);

    default TaskResponse mapTaskBasedOnRole(Task task, Role role) {
        var response = entityToDto(task);
        if (role.equals(Role.MANAGER)) {
            response.setCreatedBy(null);
        } else if (role.equals(Role.EMPLOYEE)) {
            response.setAssignedUser(null);
        }
        return response;
    }
}
