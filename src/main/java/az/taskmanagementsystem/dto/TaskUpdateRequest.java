package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.enums.Priority;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TaskUpdateRequest {

    private String title;

    private String description;

    //    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Invalid priority")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "LOW|MEDIUM|HIGH")
    private Priority priority;

    @Future(message = "Deadline must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime deadline;

    private String tags;
}
