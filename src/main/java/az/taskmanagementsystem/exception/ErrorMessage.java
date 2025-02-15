package az.taskmanagementsystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    USER_NOT_FOUND("User Not Found"),

    GENRE_NOT_FOUND("Genre not found"),

    MOVIE_NOT_FOUND("Movie not found"),

    TOKEN_NOT_FOUND("Token not found"),

    INVALID_TOKEN("Token is invalid"),

    INVALID_AUTHENTICATION_CREDENTIALS("Invalid authentication credentials"),

    USER_ALREADY_EXIST("User already exists");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }

}
