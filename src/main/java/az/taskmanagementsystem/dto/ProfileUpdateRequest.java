package az.taskmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDate;

@Getter
public class ProfileUpdateRequest {

    private String fullName;

    @Past(message = "Date of birth must be in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;
}
