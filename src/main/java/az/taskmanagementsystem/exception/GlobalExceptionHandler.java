package az.taskmanagementsystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static az.taskmanagementsystem.exception.ErrorMessage.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({UserNotFoundException.class, TaskNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(Exception ex, HttpServletRequest request) {
        log.error("Not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), NOT_FOUND, request);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handleConflictException(UserAlreadyExistException ex, HttpServletRequest request) {
        log.error("Conflict with username: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), CONFLICT, request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTokenException(InvalidTokenException ex, HttpServletRequest request) {
        log.error("Token related exception: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), UNAUTHORIZED, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        log.error("Invalid Authentication Credentials: {}", ex.getMessage());
        return buildErrorResponse(INVALID_AUTHENTICATION_CREDENTIALS.getMessage(), UNAUTHORIZED, request);
    }

    @ExceptionHandler(InvalidRoleTypeException.class)
    public ResponseEntity<ApiErrorResponse> handleRoleTypeMismatchException(InvalidRoleTypeException ex, HttpServletRequest request) {
        log.error("Invalid enum value provided: {}", ex.getMessage());
        return buildErrorResponse("Invalid role. Allowed values: ADMIN, MANAGER, EMPLOYEE", BAD_REQUEST, request);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex, HttpServletRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        return buildErrorResponse(UNAUTHORIZED_ACCESS.getMessage(), UNAUTHORIZED, request);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedOperationException(UnsupportedOperationException ex, HttpServletRequest request) {
        log.error("Unsupported operation: {}", ex.getMessage());
        return buildErrorResponse(UNSUPPORTED_OPERATION.getMessage(), BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("Message is not readable: {}", ex.getMessage());
        return buildErrorResponse(INCORRECT_DATE_FORMAT.getMessage(), BAD_REQUEST, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(RuntimeException ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage());
        return buildErrorResponse("An unexpected error occurred.", INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(response);
    }
}
