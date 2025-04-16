package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
public class TaskResponse implements Serializable {

    private Long id;

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private LocalDateTime deadline;

    private String tags;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String assignedUser;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String createdBy;
}
