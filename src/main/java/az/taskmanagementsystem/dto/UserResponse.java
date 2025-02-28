package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.entity.Task;
import az.taskmanagementsystem.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UserResponse {

    private String fullName;

    private String email;

    private Role role;

    private boolean isEnabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Task> createdTasks;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Task> assignedTasks;
}
