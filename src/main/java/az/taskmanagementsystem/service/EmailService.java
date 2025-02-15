package az.taskmanagementsystem.service;

import az.taskmanagementsystem.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String projectEmail;

    public void sendForgotPasswordLink(User user, String token) {

        String updatePassUrl = "https://localhost:8080/api/v1/auth/update-password?token=";
        String fullUrl = updatePassUrl + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(projectEmail);
        message.setTo(user.getEmail());
        message.setSubject("Reset Password");
        message.setText("Click the following link to reset your password:\n" + fullUrl);

        javaMailSender.send(message);
    }

    public void sendRegistrationLink(User user, String token) {

        String verifyAccountUrl = "https://localhost:8080/api/v1/auth/verify-register?token=";
        String fullUrl = verifyAccountUrl + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(projectEmail);
        message.setTo(user.getEmail());
        message.setSubject("Please verify your registration!");
        message.setText("Please click the link below to verify your registration:\n" + fullUrl);

        javaMailSender.send(message);
    }
}
