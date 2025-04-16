package az.taskmanagementsystem.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange("emailExchange");
    }

    @Bean
    public Queue registrationVerificationQueue() {
        return new Queue("registrationVerificationQueue", true);
    }

    @Bean
    public Queue forgotPasswordVerificationQueue() {
        return new Queue("forgotPasswordVerificationQueue", true);
    }

    @Bean
    public Queue reminderDLQ() {
        return new Queue("reminderDLQ", true);
    }

    @Bean
    public Queue reminderQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 86400000); // 24 hours
        args.put("x-dead-letter-exchange", "emailExchange");
        args.put("x-dead-letter-routing-key", "task.deadline.reminder");
        return new Queue("reminderQueue", true, false, false, args);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(reminderDLQ()).to(emailExchange()).with("task.deadline.reminder");
    }

    @Bean
    public Binding registrationBinding(DirectExchange emailExchange, Queue registrationVerificationQueue) {
        return BindingBuilder.bind(registrationVerificationQueue).to(emailExchange).with("registration.verify");
    }

    @Bean
    public Binding forgotPasswordBinding(DirectExchange emailExchange, Queue forgotPasswordVerificationQueue) {
        return BindingBuilder.bind(forgotPasswordVerificationQueue).to(emailExchange).with("forgotPassword.verify");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}