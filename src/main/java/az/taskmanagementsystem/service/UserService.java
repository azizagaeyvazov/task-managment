package az.taskmanagementsystem.service;

import az.taskmanagementsystem.dto.ProfileUpdateRequest;
import az.taskmanagementsystem.dto.UserResponse;
import az.taskmanagementsystem.entity.User;
import az.taskmanagementsystem.enums.Role;
import az.taskmanagementsystem.exception.UnauthorizedAccessException;
import az.taskmanagementsystem.exception.UserNotFoundException;
import az.taskmanagementsystem.mapper.UserMapper;
import az.taskmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper mapper;

    private final AuthenticationService authenticationService;

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    @Cacheable(value = "userList")
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        var role = authenticationService.getLoggedInUser().getRole();
        return userRepository.findAllExceptAdmin()
                .stream()
                .map(user -> mapper.mapUserBasedOnRole(user, role))
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "userList", allEntries = true)
    @CachePut(value = "users", key = "@authenticationService.loggedInUser.email")
    public UserResponse updateProfile(ProfileUpdateRequest request){
        var user = authenticationService.getLoggedInUser();
        user = userRepository.findById(user.getId()).orElseThrow(null);//
        mapper.updateUserProfile(request, user);
        userRepository.save(user);
        return mapper.entityToDto(user);
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#email"),
            @CacheEvict(value = "userList", allEntries = true)
    })
    public void deleteUserByEmail(String email) {
        var admin = authenticationService.getLoggedInUser();
        if (!admin.getRole().equals(Role.ADMIN)) throw new UnauthorizedAccessException();
        var user = getByEmail(email);
        userRepository.delete(user);
    }
}
