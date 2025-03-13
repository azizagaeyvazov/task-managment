package az.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String projectEmail;

    public void sendForgotPasswordLink(String email, String token) {

        String updatePassUrl = "https://localhost:8080/api/v1/auth/update-password?token=";
        String fullUrl = updatePassUrl + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(projectEmail);
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Click the following link to reset your password:\n" + fullUrl);
        javaMailSender.send(message);
    }

    public void sendRegistrationLink(String email, String token) {

        String verifyAccountUrl = "https://localhost:8080/api/v1/auth/verify-register?token=";
        String fullUrl = verifyAccountUrl + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(projectEmail);
        message.setTo(email);
        message.setSubject("Please verify your registration!");
        message.setText( "Please click the link below to verify your registration:\n" + fullUrl);
        javaMailSender.send(message);
    }

    public void sendDeadlineNotification(String email, String deadline, String taskTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(projectEmail);
        message.setTo(email);
        message.setSubject("Deadline notifier");
        message.setText("Hurry to complete '" + taskTitle + "' until " + deadline);
        javaMailSender.send(message);
        System.out.println("The notification message is sent to email");
    }
}
