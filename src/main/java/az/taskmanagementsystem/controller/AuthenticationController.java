package az.taskmanagementsystem.controller;

import az.taskmanagementsystem.dto.*;
import az.taskmanagementsystem.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.ok("Please check your email address to complete the registration process!");
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<String> verifyRegistration(@RequestParam String token) {
        authenticationService.verifyRegistration(token);
        return ResponseEntity.ok("The registration has been completed successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

    @PostMapping("/new-token")
    public ResponseEntity<AuthenticationResponse> getNewAccessToken(@Valid @RequestBody RefreshTokenDto request) {
        return ResponseEntity.ok(authenticationService.getNewAccessToken(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authenticationService.forgotPassword(email);
        return ResponseEntity.ok("The verification link has been sent to your email. Please check your inbox to verify your account!");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @Valid @RequestBody PasswordResetRequest request) {
        authenticationService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok("The password has been updated successfully.");
    }

    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        authenticationService.updatePassword(request);
        return ResponseEntity.ok("The password has been updated successfully.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody LogoutRequest request) {
        authenticationService.logoutUser(request);
        return ResponseEntity.ok("You logged out successfully.");
    }
}













