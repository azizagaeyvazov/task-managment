package az.taskmanagementsystem.mapper;

import az.taskmanagementsystem.dto.TaskRequest;
import az.taskmanagementsystem.dto.TaskResponse;
import az.taskmanagementsystem.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskResponse entityToDto(Task task);

    Task dtoToEntity(TaskRequest request);

    void updateTask(@MappingTarget Task task, TaskRequest request);
}
