package az.taskmanagementsystem.service;

import az.taskmanagementsystem.entity.User;
import az.taskmanagementsystem.exception.UserNotFoundException;
import az.taskmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }
}
