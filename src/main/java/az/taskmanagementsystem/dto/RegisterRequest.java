package az.taskmanagementsystem.dto;

import az.taskmanagementsystem.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank(message = "name is required")
    private String fullName;

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email format")
//    @Pattern(regexp = ".*\\.(com|org|net|edu|ru)$", message = "Email must end with .com, .org, .net, .edu or .ru")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password length must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=.]).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@#$%^&+=.)"
    )
    private String password;

    private Role role;
}
