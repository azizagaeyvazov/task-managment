package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import lombok.Setter;

import java.time.LocalDate;

@Setter
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private LocalDate deadline;

    private String tags;

//    private User assignedUser;

//    private User createdBy;
}
