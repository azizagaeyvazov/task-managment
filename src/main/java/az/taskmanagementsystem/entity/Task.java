package az.taskmanagementsystem.entity;

import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDate deadline;

    private String tags;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User assignedUser;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
