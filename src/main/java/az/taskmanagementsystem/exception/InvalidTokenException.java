package az.taskmanagementsystem.exception;

import static az.taskmanagementsystem.exception.ErrorMessage.INVALID_TOKEN;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException() {
        super(INVALID_TOKEN.getMessage());
    }
}
