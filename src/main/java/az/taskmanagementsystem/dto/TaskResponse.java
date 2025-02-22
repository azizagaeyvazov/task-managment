package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.entity.User;
import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private LocalDate deadline;

    private String tags;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private User assignedUser;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private User createdBy;
}
