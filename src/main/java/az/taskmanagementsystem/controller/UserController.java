package az.taskmanagementsystem.controller;

import az.taskmanagementsystem.dto.ProfileUpdateRequest;
import az.taskmanagementsystem.dto.UserResponse;
import az.taskmanagementsystem.dto.UserUpdateRequest;
import az.taskmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController("/api/v1/user")
public class UserController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(service.getAll());
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updateUserByAdmin(@RequestParam String email,
                                                          @Valid @RequestBody UserUpdateRequest request){
        return ResponseEntity.ok(service.updateUserByAdmin(email, request));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request){
        return ResponseEntity.ok(service.updateProfile(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserByAdmin(@RequestParam String email){
        service.deleteUserByEmail(email);
        return ResponseEntity.ok().build();
    }
}
