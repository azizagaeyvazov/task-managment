package az.taskmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RefreshTokenDto {

    @NotNull(message = "Refresh token must be sent")
    private String refreshToken;
}
