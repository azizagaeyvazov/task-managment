package az.taskmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class LoginRequest {

    @NotBlank(message = "email is required")
    private String email;

    @NotEmpty(message = "password is required")
    private String password;
}
