package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskRequest {

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private LocalDate deadline;

    private String tags;

//    private User assignedUser;

//    private User createdBy;
}
