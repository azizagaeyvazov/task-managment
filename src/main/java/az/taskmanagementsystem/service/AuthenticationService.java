package az.taskmanagementsystem.service;

import az.taskmanagementsystem.dto.AuthenticationResponse;
import az.taskmanagementsystem.dto.LoginRequest;
import az.taskmanagementsystem.dto.RegisterRequest;
import az.taskmanagementsystem.dto.ResetPasswordRequest;
import az.taskmanagementsystem.entity.UUIDToken;
import az.taskmanagementsystem.entity.User;
import az.taskmanagementsystem.exception.InvalidTokenException;
import az.taskmanagementsystem.exception.UserAlreadyExistException;
import az.taskmanagementsystem.exception.UserNotFoundException;
import az.taskmanagementsystem.mapper.UserMapper;
import az.taskmanagementsystem.rabbitmq.consumer.EmailConsumer;
import az.taskmanagementsystem.rabbitmq.producer.EmailProducer;
import az.taskmanagementsystem.repository.UUIDTokenRepository;
import az.taskmanagementsystem.repository.UserRepository;
import az.taskmanagementsystem.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserMapper userMapper;

    private final JwtService jwtService;

    private final EmailProducer emailProducer;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UUIDTokenRepository UUIDTokenRepository;

    private final AuthenticationManager authenticationManager;

    @Transactional
    public void register(RegisterRequest request) {

        var userEntityOpt = userRepository.findByEmail(request.getEmail());
        var user = userEntityOpt
                .map(existingUser -> {
                    if (existingUser.isEnabled()) {
                        throw new UserAlreadyExistException();
                    }
                    userMapper.updateUserRegister(request, existingUser);
                    return existingUser;
                })
                .orElseGet(() -> userMapper.dtoToEntity(request));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var uuidTokenEntity = generateUUIDToken(user);
        user.setUuidToken(uuidTokenEntity);
        userRepository.save(user);
        emailProducer.sendRegistrationVerificationEmail(user.getEmail(), uuidTokenEntity.getToken());
    }

    @Transactional
    public void verifyRegistration(String token) {

        var uuidToken = UUIDTokenRepository.findByToken(token);
        if (uuidToken.isEmpty() || uuidToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException();
        }
        var user = uuidToken.get().getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public AuthenticationResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = (User) authentication.getPrincipal();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void forgotPassword(String email) {

        var user = userRepository.findByEmail(email).orElseThrow(
                UserNotFoundException::new);
        var uuidTokenEntity = generateUUIDToken(user);
        UUIDTokenRepository.save(uuidTokenEntity);
        emailProducer.sendForgotPasswordVerificationEmail(user.getEmail(), uuidTokenEntity.getToken());
    }

    @Transactional
    public void updatePassword(String token, String newPassword) {

        var uuidToken = UUIDTokenRepository.findByToken(token);
        if (uuidToken.isEmpty() || uuidToken.get().getExpiryDate().isBefore(LocalDateTime.now()))
            throw new InvalidTokenException();

        var user = uuidToken.get().getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public AuthenticationResponse getNewAccessToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException();
        }
        String refreshToken = authHeader.substring(7);
        String email = jwtService.extractEmail(refreshToken);
        String tokenType = jwtService.extractTokenType(refreshToken);

        var userDetails = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (!"REFRESH".equals(tokenType) || !jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new InvalidTokenException();
        }
        String accessToken = jwtService.generateAccessToken(userDetails);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        var user = getLoggedInUser();
        var newPassword = passwordEncoder.encode(request.getNewPassword());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Wrong password!");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    private UUIDToken generateUUIDToken(User user) {
        String token = UUID.randomUUID().toString();
        return UUIDToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    public User getLoggedInUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        var principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new IllegalStateException("Authenticated principal is not of type User");
        }
    }
}
