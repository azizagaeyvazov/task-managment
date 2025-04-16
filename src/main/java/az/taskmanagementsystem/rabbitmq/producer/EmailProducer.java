package az.taskmanagementsystem.rabbitmq.producer;

import az.taskmanagementsystem.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendRegistrationVerificationEmail(String email, String token) {
        Map<String, String> message = new HashMap<>();
        message.put("email", email);
        message.put("token", token);
        rabbitTemplate.convertAndSend("emailExchange", "registration.verify", message);
    }

    public void sendForgotPasswordVerificationEmail(String email, String token) {
        Map<String, String> message = new HashMap<>();
        message.put("email", email);
        message.put("token", token);
        rabbitTemplate.convertAndSend("emailExchange", "forgotPassword.verify", message);
        System.out.println("The message is sent to rabbitmq");
    }


    public void sendTaskDeadlineNotification(Task task) {
        long ttl = calculateDynamicTTL(task.getDeadline());
        if (ttl <= 0) return;
        rabbitTemplate.convertAndSend(
                "emailExchange",
                "task.deadline.notify",
                task.getId(),
                msg -> {
                    msg.getMessageProperties().setExpiration(String.valueOf(ttl));
                    return msg;
                }
        );
    }

    private long calculateDynamicTTL(LocalDateTime deadline) {
        LocalDateTime reminderTime = deadline.minusDays(1);
        return Duration.between(LocalDateTime.now(), reminderTime).toMillis();
    }

}
