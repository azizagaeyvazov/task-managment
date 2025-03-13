package az.taskmanagementsystem.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RedisHash
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
    }


    public void sendTaskDeadlineNotification(String email, String taskTitle, LocalDateTime deadline) {
        long ttl = calculateDynamicTTL(deadline);
        if (email == null || ttl <= 0) return;
        Map<String, String> message = new HashMap<>();
        message.put("email", email);
        message.put("taskTitle", taskTitle);
        message.put("deadline", deadline.toString());
        rabbitTemplate.convertAndSend(
                "emailExchange",
                "task.deadline.notify",
                message,
                msg -> {
                    msg.getMessageProperties().setExpiration(String.valueOf(ttl));
                    return msg;
                }
        );
        System.out.println("The notification message is sent to queue");
    }

    private long calculateDynamicTTL(LocalDateTime deadline) {
        LocalDateTime reminderTime = deadline.minusDays(1);
        return Duration.between(LocalDateTime.now(), reminderTime).toMillis();
    }

}
