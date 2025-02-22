package az.taskmanagementsystem.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super(ErrorMessage.UNAUTHORIZED_ACCESS.getMessage());
    }
}
