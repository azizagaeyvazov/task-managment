package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.enums.Priority;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskCreateRequest {

    @NotEmpty(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Priority can not be null")
//    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Invalid priority")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "LOW|MEDIUM|HIGH")
    private Priority priority;

    @NotNull(message = "Deadline cannot be null")
    @Future(message = "Deadline must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime deadline;

    private String tags;

    private String assignedUserEmail;
}
