package az.taskmanagementsystem.rabbitmq.consumer;

import az.taskmanagementsystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "registrationVerificationQueue", concurrency = "3-10")
    public void handleRegistrationVerificationEmail(Map<String, String> message) {
        emailService.sendRegistrationLink(message.get("email"), message.get("token"));
    }

    @RabbitListener(queues = "forgotPasswordVerificationQueue", concurrency = "3-10")
    public void handleForgotPasswordVerificationEmail(Map<String, String> message) {
        emailService.sendForgotPasswordLink(message.get("email"), message.get("token"));
    }

    @RabbitListener(queues = "taskDeadlineNotificationQueue", concurrency = "3-10")
    public void handleTaskDeadlineNotification(Map<String, String> message) {
        System.out.println("The notification message is received from queue");
        emailService.sendDeadlineNotification(message.get("email"), message.get("deadline"), message.get("taskTitle"));
    }
}
