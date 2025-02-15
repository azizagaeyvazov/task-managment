package az.taskmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class AuthenticationResponse {

    private String accessToken;

    private String refreshToken;
}
