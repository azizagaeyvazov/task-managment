package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskCreateRequest {

    @NotEmpty(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Priority can not be null")
    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Invalid priority")
    private Priority priority;

    @NotNull(message = "Deadline cannot be null")
    @Future(message = "Deadline must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate deadline;

    private String tags;

    private String assignedUserEmail;
}
