package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.enums.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskUpdateRequest {

    private String title;

    private String description;

    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Invalid priority")
    private Priority priority;

    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;

    private String tags;

    private String assignedUserEmail;
}
