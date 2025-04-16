package az.taskmanagementsystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
        log.error("NotFoundException: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), NOT_FOUND, request);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handleConflictException(UserAlreadyExistException ex, HttpServletRequest request) {
        log.error("ConflictWithUsernameException: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), CONFLICT, request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTokenException(InvalidTokenException ex, HttpServletRequest request) {
        log.error("InvalidTokenException: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), UNAUTHORIZED, request);
    }

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<ApiErrorResponse> handleSignInExceptions(Exception ex, HttpServletRequest request) {
        log.error("InvalidAuthenticationCredentialsException: {}", ex.getMessage());
        return buildErrorResponse(INVALID_AUTHENTICATION_CREDENTIALS.getMessage(), FORBIDDEN, request);
    }

    @ExceptionHandler(InvalidRoleTypeException.class)
    public ResponseEntity<ApiErrorResponse> handleRoleTypeMismatchException(InvalidRoleTypeException ex, HttpServletRequest request) {
        log.error("InvalidEnumValueException: {}", ex.getMessage());
        return buildErrorResponse("Invalid role. Allowed values: ADMIN, MANAGER, EMPLOYEE", BAD_REQUEST, request);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex, HttpServletRequest request) {
        log.error("UnauthorizedAccessException: {}", ex.getMessage());
        return buildErrorResponse(UNAUTHORIZED_ACCESS.getMessage(), UNAUTHORIZED, request);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedOperationException(UnsupportedOperationException ex, HttpServletRequest request) {
        log.error("UnsupportedOperationException: {}", ex.getMessage());
        return buildErrorResponse(UNSUPPORTED_OPERATION.getMessage(), BAD_REQUEST, request);
    }

//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    @ExceptionHandler(DateTimeParseException.class)
//    public ResponseEntity<ApiErrorResponse> handleDateAndDateTimeParseException(Exception ex, HttpServletRequest request) {
//        log.error("Message is not readable: {}", ex.getMessage());
//        return buildErrorResponse(ex.getMessage(), BAD_REQUEST, request);
//    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("UnexpectedError: {}", ex.getMessage());
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), INTERNAL_SERVER_ERROR, request);
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
