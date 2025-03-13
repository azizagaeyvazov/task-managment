package az.taskmanagementsystem.entity;

import az.taskmanagementsystem.enums.Priority;
import az.taskmanagementsystem.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDateTime deadline;

    private String tags;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
