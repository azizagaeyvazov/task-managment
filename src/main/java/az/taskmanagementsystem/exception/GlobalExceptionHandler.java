package az.taskmanagementsystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static az.taskmanagementsystem.exception.ErrorMessage.INVALID_AUTHENTICATION_CREDENTIALS;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> map = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> map.put(error.getField(), error.getDefaultMessage())
        );
        return map;
    }

    @ExceptionHandler({TokenNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        log.error("NotFoundException: {}", ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                NOT_FOUND.value(),
                NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    @ResponseStatus(CONFLICT)
    public ResponseEntity<ApiErrorResponse> handle(UserAlreadyExistException ex, HttpServletRequest request) {
        log.error("Conflict with username: {}", ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                CONFLICT.value(),
                CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidTokenException.class})
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> handle(InvalidTokenException ex, HttpServletRequest request) {
        log.error("Token related exception: {}", ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                BAD_REQUEST.value(),
                ex.getMessage(),
                BAD_REQUEST.getReasonPhrase(),
                request.getRequestURI()
                );

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<ApiErrorResponse> handle(BadCredentialsException ex, HttpServletRequest request) {
        log.error("InvalidAuthenticationCredentialsException: {}", String.valueOf(ex));

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                UNAUTHORIZED.value(),
                INVALID_AUTHENTICATION_CREDENTIALS.getMessage(),
                UNAUTHORIZED.getReasonPhrase(),
                request.getRequestURI()
                );

        return new ResponseEntity<>(response, UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body("Invalid role. Allowed values: ADMIN, MANAGER, EMPLOYEE");
    }
}
