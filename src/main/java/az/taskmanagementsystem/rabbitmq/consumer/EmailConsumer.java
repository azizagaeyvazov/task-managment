package az.taskmanagementsystem.rabbitmq.consumer;

import az.taskmanagementsystem.entity.Task;
import az.taskmanagementsystem.service.EmailService;
import az.taskmanagementsystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    private final TaskService taskService;

    @RabbitListener(queues = "registrationVerificationQueue", concurrency = "3-10")
    public void handleRegistrationVerificationEmail(Map<String, String> message) {
        emailService.sendRegistrationLink(message.get("email"), message.get("token"));
    }

    @RabbitListener(queues = "forgotPasswordVerificationQueue", concurrency = "3-10")
    public void handleForgotPasswordVerificationEmail(Map<String, String> message) {
        emailService.sendForgotPasswordLink(message.get("email"), message.get("token"));
    }

    @RabbitListener(queues = "reminderDLQ", concurrency = "3-10")
    public void handleTaskDeadlineNotification(Long taskId) {
        var task = taskService.getTaskById(taskId);
        if (isDeadlineApproachingWithin24Hours(task.getDeadline())) {
            emailService.sendDeadlineNotification(task.getAssignedUser().getEmail(),
                    task.getDeadline().toString(),
                    task.getTitle());
        }
    }

    private boolean isDeadlineApproachingWithin24Hours(LocalDateTime deadline){
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, deadline);
        return deadline.isAfter(now) && duration.toMillis() <= 86400000; //86_400_000ms = 24hours
    }
}
