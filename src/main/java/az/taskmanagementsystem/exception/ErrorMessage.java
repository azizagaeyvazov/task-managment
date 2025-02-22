package az.taskmanagementsystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    USER_NOT_FOUND("User not found"),

    TASK_NOT_FOUND("Task not found"),

    INVALID_TOKEN("Token is invalid"),

    INVALID_AUTHENTICATION_CREDENTIALS("Invalid authentication credentials"),

    USER_ALREADY_EXIST("User already exists"),

    INVALID_ROLE_TYPE("Role type mismatches"),

    UNAUTHORIZED_ACCESS("Access denied");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }

}
