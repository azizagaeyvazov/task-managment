package az.taskmanagementsystem.exception;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException() {
        super(ErrorMessage.INVALID_TOKEN.getMessage());
    }
}
