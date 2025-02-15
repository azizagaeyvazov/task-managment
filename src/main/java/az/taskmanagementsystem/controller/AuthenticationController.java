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
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify-registration")
    public ResponseEntity<Void> verifyRegistration(@RequestParam String token) {
        authenticationService.verifyRegistration(token);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        authenticationService.forgotPassword(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestParam String token, @Valid @RequestBody UpdatePasswordRequest request) {
        authenticationService.updatePassword(token, request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        tokenBlacklistService.blacklistToken(request);
        return ResponseEntity.ok().build();
    }


}













