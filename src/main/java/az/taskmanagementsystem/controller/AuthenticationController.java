package az.taskmanagementsystem.controller;

import az.taskmanagementsystem.dto.*;
import az.taskmanagementsystem.security.JwtService;
import az.taskmanagementsystem.security.TokenBlacklistService;
import az.taskmanagementsystem.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/registration")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.ok("Please check your email address to complete the registration process!");
    }

    @GetMapping("/verify-registration")
    public ResponseEntity<String> verifyRegistration(@RequestParam String token) {
        authenticationService.verifyRegistration(token);
        return ResponseEntity.ok("The registration has been completed successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

    @PostMapping("/new-token")
    public ResponseEntity<AuthenticationResponse> getNewAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(authenticationService.getNewAccessToken(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authenticationService.forgotPassword(email);
        return ResponseEntity.ok("The verification link has been sent to your email. Please check your inbox to verify your account!");
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String token, @Valid @RequestBody UpdatePasswordRequest request) {
        authenticationService.updatePassword(token, request.getNewPassword());
        return ResponseEntity.ok("The password has been updated successfully.");
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok("The password has been updated successfully.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        tokenBlacklistService.blacklistToken(request);
        return ResponseEntity.ok().build();
    }
}













