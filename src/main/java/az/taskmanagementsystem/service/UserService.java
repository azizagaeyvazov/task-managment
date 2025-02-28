package az.taskmanagementsystem.service;

import az.taskmanagementsystem.dto.UserResponse;
import az.taskmanagementsystem.dto.UserUpdateRequest;
import az.taskmanagementsystem.entity.User;
import az.taskmanagementsystem.exception.UserNotFoundException;
import az.taskmanagementsystem.mapper.UserMapper;
import az.taskmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    private final AuthenticationService authenticationService;

    private final PasswordEncoder passwordEncoder;

    public User getByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public List<UserResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

//    public UserResponse updateUser(UserUpdateRequest request) {
//
//        var user = authenticationService.getLoggedInUser();
//        mapper.updateUserEntity(request, user);
//        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword));
//        repository.save(user);
//        return mapper.entityToDto(user);
//    }

    public void deleteUserByEmail(String email) {
        var user = getByEmail(email);
        repository.delete(user);
    }
}
