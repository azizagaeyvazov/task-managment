package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class UserResponse implements Serializable {

    private Long id;

    private String fullName;

    private String email;

    private Role role;

    private boolean isEnabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TaskResponse> createdTasks;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TaskResponse> assignedTasks;
}
